/*
 * NeoCatroid
 * Copyright (C) 2026 The NeoCatroid Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 */

package org.catrobat.catroid.neoscript;

import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.RaspiInterruptScript;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.UserDefinedScript;
import org.catrobat.catroid.content.WhenBackgroundChangesScript;
import org.catrobat.catroid.content.WhenBounceOffScript;
import org.catrobat.catroid.content.WhenConditionScript;
import org.catrobat.catroid.content.WhenFirebaseChangedScript;
import org.catrobat.catroid.content.WhenGamepadButtonScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.CompositeBrick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.NoteBrick;
import org.catrobat.catroid.content.bricks.UnknownBrick;
import org.catrobat.catroid.content.bricks.UserListBrick;
import org.catrobat.catroid.content.bricks.UserVariableBrickWithFormula;
import org.catrobat.catroid.formulaeditor.Formula;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class NeoScriptImporter {

	private NeoScriptImporter() {
	}

	public enum ImportStrategy {
		SKIP_DUPLICATES,
		REPLACE_DUPLICATES,
		APPEND_ALL,
		REPLACE_ALL
	}

	public static final class ImportResult {
		public final List<Script> added = new ArrayList<>();
		public final List<Script> skipped = new ArrayList<>();
		public final List<Script> replaced = new ArrayList<>();
	}

	public static ImportResult importScripts(NeoScriptFile file, Project project, Sprite targetSprite,
			boolean overwrite) throws NeoScriptException {
		return importScripts(file, project, targetSprite,
				overwrite ? ImportStrategy.REPLACE_DUPLICATES : ImportStrategy.SKIP_DUPLICATES);
	}

	public static ImportResult importScripts(NeoScriptFile file, Project project, Sprite targetSprite,
			ImportStrategy strategy) throws NeoScriptException {
		if (file == null || file.getScripts() == null || file.getScripts().isEmpty()) {
			throw new NeoScriptException("No scripts to import");
		}
		if (targetSprite == null) {
			throw new NeoScriptException("No target object to import into");
		}

		ImportResult result = new ImportResult();

		java.util.Map<String, Object> variableValues = file.getUserVariableValues();
		java.util.Map<String, java.util.List<Object>> listValues = file.getUserListValues();

		if (strategy == ImportStrategy.REPLACE_ALL) {
			List<Script> imported = new ArrayList<>();
			try {
				for (Script original : file.getScripts()) {
					Script clone = original.clone();
					NeoScriptUserData.relink(clone, project, targetSprite, variableValues, listValues);
					clone.setParents();
					imported.add(clone);
				}
			} catch (CloneNotSupportedException e) {
				throw new NeoScriptException("Failed to clone script: " + e.getMessage(), e);
			}
			targetSprite.getScriptList().clear();
			for (Script s : imported) {
				targetSprite.addScript(s);
				result.added.add(s);
			}
			return result;
		}

		Set<String> targetSignatures = new HashSet<>();
		for (Script script : targetSprite.getScriptList()) {
			String sig = scriptSignature(script);
			if (sig != null) {
				targetSignatures.add(sig);
			}
		}

		if (strategy == ImportStrategy.REPLACE_DUPLICATES) {
			Set<String> importedParameterizedSigs = new HashSet<>();
			for (Script s : file.getScripts()) {
				String sig = scriptSignature(s);
				if (sig != null) {
					importedParameterizedSigs.add(sig);
				}
			}
			List<Script> toRemove = new ArrayList<>();
			for (Script script : targetSprite.getScriptList()) {
				String sig = scriptSignature(script);
				if (sig != null && importedParameterizedSigs.contains(sig)) {
					toRemove.add(script);
				}
			}
			List<Script> cloned = new ArrayList<>();
			try {
				for (Script original : file.getScripts()) {
					Script clone = original.clone();
					NeoScriptUserData.relink(clone, project, targetSprite, variableValues, listValues);
					clone.setParents();
					cloned.add(clone);
				}
			} catch (CloneNotSupportedException e) {
				throw new NeoScriptException("Failed to clone script: " + e.getMessage(), e);
			}
			targetSprite.getScriptList().removeAll(toRemove);
			for (Script s : cloned) {
				targetSprite.addScript(s);
				result.added.add(s);
				if (scriptSignature(s) != null) {
					result.replaced.add(s);
				}
			}
			return result;
		}

		List<Script> prepared = new ArrayList<>();
		try {
			for (Script original : file.getScripts()) {
				Script clone = original.clone();
				NeoScriptUserData.relink(clone, project, targetSprite, variableValues, listValues);
				clone.setParents();
				prepared.add(clone);
			}
		} catch (CloneNotSupportedException e) {
			throw new NeoScriptException("Failed to clone script: " + e.getMessage(), e);
		}
		for (Script clone : prepared) {
			String signature = scriptSignature(clone);

			if (strategy == ImportStrategy.APPEND_ALL) {
				targetSprite.addScript(clone);
				result.added.add(clone);
			} else if (strategy == ImportStrategy.SKIP_DUPLICATES) {
				boolean duplicate = signature != null && targetSignatures.contains(signature);
				if (duplicate) {
					result.skipped.add(clone);
				} else {
					targetSprite.addScript(clone);
					result.added.add(clone);
				}
			}
		}
		return result;
	}

	public static String scriptSignature(Script script) {
		if (script instanceof BroadcastScript) {
			return "BroadcastScript#" + ((BroadcastScript) script).getBroadcastMessage() + "#" + contentFingerprint(script);
		}
		if (script instanceof WhenBackgroundChangesScript) {
			org.catrobat.catroid.common.LookData look = ((WhenBackgroundChangesScript) script).getLook();
			return "WhenBackgroundChangesScript#" + (look != null ? look.getName() : "") + "#" + contentFingerprint(script);
		}
		if (script instanceof WhenGamepadButtonScript) {
			return "WhenGamepadButtonScript#" + ((WhenGamepadButtonScript) script).getAction() + "#" + contentFingerprint(script);
		}
		if (script instanceof WhenFirebaseChangedScript) {
			WhenFirebaseChangedScript fb = (WhenFirebaseChangedScript) script;
			String bucket = "";
			if (fb.getFormulaMap() != null && fb.getFormulaMap().containsKey(org.catrobat.catroid.content.bricks.Brick.BrickField.FIREBASE_TRIGGER_BUCKET)) {
				org.catrobat.catroid.formulaeditor.Formula f = fb.getFormulaMap().get(org.catrobat.catroid.content.bricks.Brick.BrickField.FIREBASE_TRIGGER_BUCKET);
				if (f != null) {
					bucket = f.getTrimmedFormulaString(null);
				}
			}
			String path = "";
			if (fb.getFormulaMap() != null && fb.getFormulaMap().containsKey(org.catrobat.catroid.content.bricks.Brick.BrickField.FIREBASE_TRIGGER_PATH)) {
				org.catrobat.catroid.formulaeditor.Formula f = fb.getFormulaMap().get(org.catrobat.catroid.content.bricks.Brick.BrickField.FIREBASE_TRIGGER_PATH);
				if (f != null) {
					path = f.getTrimmedFormulaString(null);
				}
			}
			return "WhenFirebaseChangedScript#" + bucket + "/" + path + "#" + contentFingerprint(script);
		}
		if (script instanceof RaspiInterruptScript) {
			RaspiInterruptScript raspi = (RaspiInterruptScript) script;
			return "RaspiInterruptScript#" + raspi.getPin() + "+" + raspi.getEventValue() + "#" + contentFingerprint(script);
		}
		if (script instanceof UserDefinedScript) {
			UserDefinedScript uds = (UserDefinedScript) script;
			return "UserDefinedScript#" + uds.getUserDefinedBrickID() + "#" + contentFingerprint(script);
		}
		if (script instanceof WhenBounceOffScript) {
			String target = ((WhenBounceOffScript) script).getSpriteToBounceOffName();
			return "WhenBounceOffScript#" + (target != null ? target : "") + "#" + contentFingerprint(script);
		}
		if (script instanceof WhenConditionScript) {
			WhenConditionScript conditionScript = (WhenConditionScript) script;
			String condition = "";
			try {
				Formula formula = conditionScript.getFormulaMap() != null
						? conditionScript.getFormulaMap().get(Brick.BrickField.IF_CONDITION) : null;
				if (formula != null) {
					condition = formula.getTrimmedFormulaString(null);
				}
			} catch (Exception ignored) {
			}
			return "WhenConditionScript#" + condition + "#" + contentFingerprint(script);
		}
		return script.getClass().getSimpleName() + "#" + contentFingerprint(script);
	}

	public static boolean hasUnknownBricks(NeoScriptFile file) {
		if (file == null || file.getScripts() == null) {
			return false;
		}
		for (Script script : file.getScripts()) {
			if (script != null && containsUnknownBrick(script.getBrickList(), new HashSet<>())) {
				return true;
			}
		}
		return false;
	}

	public static void replaceUnknownBricks(NeoScriptFile file) {
		if (file == null || file.getScripts() == null) {
			return;
		}
		for (Script script : file.getScripts()) {
			if (script != null && script.getBrickList() != null) {
				replaceUnknownInList(script.getBrickList(), new HashSet<>());
			}
		}
	}

	private static boolean containsUnknownBrick(List<Brick> bricks, Set<Object> visited) {
		if (bricks == null) {
			return false;
		}
		for (Brick brick : bricks) {
			if (brick == null || !visited.add(brick)) {
				continue;
			}
			if (brick instanceof UnknownBrick) {
				return true;
			}
			if (brick instanceof CompositeBrick) {
				CompositeBrick composite = (CompositeBrick) brick;
				if (containsUnknownBrick(composite.getNestedBricks(), visited)) {
					return true;
				}
				if (composite.hasSecondaryList()
						&& containsUnknownBrick(composite.getSecondaryNestedBricks(), visited)) {
					return true;
				}
			}
		}
		return false;
	}

	private static void replaceUnknownInList(List<Brick> bricks, Set<Object> visited) {
		if (bricks == null) {
			return;
		}
		for (int i = 0; i < bricks.size(); i++) {
			Brick brick = bricks.get(i);
			if (brick == null || !visited.add(brick)) {
				continue;
			}
			if (brick instanceof UnknownBrick) {
				bricks.set(i, new NoteBrick("This block is not supported"));
			} else if (brick instanceof CompositeBrick) {
				CompositeBrick composite = (CompositeBrick) brick;
				replaceUnknownInList(composite.getNestedBricks(), visited);
				if (composite.hasSecondaryList()) {
					replaceUnknownInList(composite.getSecondaryNestedBricks(), visited);
				}
			}
		}
	}

	private static String contentFingerprint(Script script) {
		StringBuilder builder = new StringBuilder();
		Set<Object> visited = new HashSet<>();
		if (script.getBrickList() != null) {
			for (Brick brick : script.getBrickList()) {
				appendBrickFingerprint(builder, brick, visited);
			}
		}
		return Integer.toHexString(builder.toString().hashCode());
	}

	private static void appendBrickFingerprint(StringBuilder builder, Brick brick, Set<Object> visited) {
		if (brick == null || !visited.add(brick)) {
			return;
		}
		builder.append(brick.getClass().getSimpleName()).append(';');
		if (brick instanceof UserVariableBrickWithFormula) {
			try {
				org.catrobat.catroid.formulaeditor.UserVariable variable =
						((UserVariableBrickWithFormula) brick).getUserVariable();
				builder.append(variable != null && variable.getName() != null ? variable.getName() : "").append(';');
			} catch (Exception ignored) {
			}
		}
		if (brick instanceof UserListBrick) {
			try {
				org.catrobat.catroid.formulaeditor.UserList list = ((UserListBrick) brick).getUserList();
				builder.append(list != null && list.getName() != null ? list.getName() : "").append(';');
			} catch (Exception ignored) {
			}
		}
		if (brick instanceof FormulaBrick) {
			try {
				for (Formula formula : ((FormulaBrick) brick).getFormulas()) {
					if (formula != null) {
						builder.append(formulaText(formula)).append(';');
					}
				}
			} catch (Exception ignored) {
			}
		}
		if (brick instanceof CompositeBrick) {
			CompositeBrick composite = (CompositeBrick) brick;
			if (composite.getNestedBricks() != null) {
				for (Brick nested : composite.getNestedBricks()) {
					appendBrickFingerprint(builder, nested, visited);
				}
			}
			if (composite.hasSecondaryList() && composite.getSecondaryNestedBricks() != null) {
				for (Brick nested : composite.getSecondaryNestedBricks()) {
					appendBrickFingerprint(builder, nested, visited);
				}
			}
		}
	}

	private static String formulaText(Formula formula) {
		try {
			String text = formula.getTrimmedFormulaString(null);
			return text != null ? text : "";
		} catch (Exception e) {
			return "";
		}
	}
}
