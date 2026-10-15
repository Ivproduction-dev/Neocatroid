package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.UiUtils;

import java.lang.ref.WeakReference;

import androidx.appcompat.app.AppCompatActivity;

public class AssignScriptsBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	private int useSceneSelection = 0;
	private int replaceExistingSelection = 0;
	private int savePersistentSelection = 0;

	private static volatile WeakReference<AssignScriptsBrick> pendingPickerBrickRef;

	public AssignScriptsBrick() {
		addAllowedBrickField(BrickField.ASSIGN_SCRIPTS_FILE, R.id.brick_assign_scripts_file_edit);
		addAllowedBrickField(BrickField.ASSIGN_SCRIPTS_OBJECT, R.id.brick_assign_scripts_object_edit);
		addAllowedBrickField(BrickField.ASSIGN_SCRIPTS_SCENE, R.id.brick_assign_scripts_scene_edit);
		setFormulaWithBrickField(BrickField.ASSIGN_SCRIPTS_SCENE, new Formula(""));
	}

	public AssignScriptsBrick(String filePath, String objectName, boolean replaceExistingScripts) {
		this();
		setFormulaWithBrickField(BrickField.ASSIGN_SCRIPTS_FILE, new Formula(filePath));
		setFormulaWithBrickField(BrickField.ASSIGN_SCRIPTS_OBJECT, new Formula(objectName));
		this.replaceExistingSelection = replaceExistingScripts ? 1 : 0;
	}

	public AssignScriptsBrick(Formula filePath, Formula objectName, boolean replaceExistingScripts) {
		this();
		setFormulaWithBrickField(BrickField.ASSIGN_SCRIPTS_FILE, filePath);
		setFormulaWithBrickField(BrickField.ASSIGN_SCRIPTS_OBJECT, objectName);
		this.replaceExistingSelection = replaceExistingScripts ? 1 : 0;
	}

	@Override
	public Brick clone() throws CloneNotSupportedException {
		AssignScriptsBrick clone = (AssignScriptsBrick) super.clone();
		return clone;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_assign_scripts;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);

		CheckBox sceneCheckbox = view.findViewById(R.id.brick_assign_scripts_scene_checkbox);
		TextView sceneEdit = view.findViewById(R.id.brick_assign_scripts_scene_edit);
		TextView sceneLabel = view.findViewById(R.id.brick_assign_scripts_scene_label);

		sceneCheckbox.setChecked(useSceneSelection == 1);
		sceneEdit.setVisibility(useSceneSelection == 1 ? View.VISIBLE : View.GONE);
		sceneLabel.setVisibility(useSceneSelection == 1 ? View.VISIBLE : View.GONE);

		sceneCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
			useSceneSelection = isChecked ? 1 : 0;
			sceneEdit.setVisibility(isChecked ? View.VISIBLE : View.GONE);
			sceneLabel.setVisibility(isChecked ? View.VISIBLE : View.GONE);
		});

		Spinner replaceSpinner = view.findViewById(R.id.brick_assign_scripts_replace_spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
				R.layout.simple_spinner_item_white_text,
				new String[]{context.getString(R.string.no), context.getString(R.string.yes)});
		adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_white_text);
		replaceSpinner.setAdapter(adapter);
		replaceSpinner.setSelection(replaceExistingSelection);
		replaceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				replaceExistingSelection = position;
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		Spinner savePersistentSpinner = view.findViewById(R.id.brick_assign_scripts_save_spinner);
		ArrayAdapter<String> saveAdapter = new ArrayAdapter<>(context,
				R.layout.simple_spinner_item_white_text,
				new String[]{context.getString(R.string.no), context.getString(R.string.yes)});
		saveAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_white_text);
		savePersistentSpinner.setAdapter(saveAdapter);
		savePersistentSpinner.setSelection(savePersistentSelection);
		savePersistentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				savePersistentSelection = position;
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		TextView fileEdit = view.findViewById(R.id.brick_assign_scripts_file_edit);
		fileEdit.setOnClickListener(v -> openFilePicker(context));

		return view;
	}

	private void openFilePicker(Context context) {
		AppCompatActivity activity = UiUtils.getActivityFromView(view);
		if (activity == null) {
			return;
		}
		pendingPickerBrickRef = new WeakReference<>(this);
		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("*/*");
		intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"application/octet-stream", "text/xml"});
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
		activity.startActivityForResult(intent, org.catrobat.catroid.ui.SpriteActivity.REQUEST_NEO_SCRIPT_FILE_ASSIGN);
	}

	public static void onFilePicked(android.net.Uri uri) {
		WeakReference<AssignScriptsBrick> ref = pendingPickerBrickRef;
		if (ref == null || uri == null) {
			return;
		}
		AssignScriptsBrick brick = ref.get();
		pendingPickerBrickRef = null;
		if (brick == null) {
			return;
		}
		String path = uri.toString();
		try {
			android.content.Context ctx = brick.view != null ? brick.view.getContext() : null;
			org.catrobat.catroid.content.Project project =
					org.catrobat.catroid.ProjectManager.getInstance().getCurrentProject();
			if (ctx != null && project != null) {
				path = org.catrobat.catroid.neoscript.NeoScriptProjectFiles.storePickedModule(ctx, uri, project);
				try {
					ctx.getContentResolver().takePersistableUriPermission(uri,
							Intent.FLAG_GRANT_READ_URI_PERMISSION);
				} catch (Exception ignored) {
				}
			}
		} catch (Exception ignored) {
		}
		brick.setFormulaWithBrickField(BrickField.ASSIGN_SCRIPTS_FILE, new Formula(path));
		try {
			if (brick.view != null) {
				TextView fileEdit = brick.view.findViewById(R.id.brick_assign_scripts_file_edit);
				if (fileEdit != null) {
					fileEdit.setText(path);
				}
			}
		} catch (Exception ignored) {
		}
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createAssignScriptsAction(sprite, sequence,
				getFormulaWithBrickField(BrickField.ASSIGN_SCRIPTS_FILE),
				getFormulaWithBrickField(BrickField.ASSIGN_SCRIPTS_OBJECT),
				isUseScene() ? getFormulaWithBrickField(BrickField.ASSIGN_SCRIPTS_SCENE) : null,
				isReplaceExistingScripts(),
				isSavePersistent()));
	}

	public boolean isReplaceExistingScripts() {
		return replaceExistingSelection == 1;
	}

	public boolean isSavePersistent() {
		return savePersistentSelection == 1;
	}

	public boolean isUseScene() {
		return useSceneSelection == 1;
	}
}
