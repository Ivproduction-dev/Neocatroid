/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.actions;

import android.util.Log;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.utils.ShowTextUtils;

public class SetThreadColorAction extends TemporalAction {

	private Scope scope;
	private Formula color;
	private Sprite sprite;

	@Override
	protected void update(float delta) {
		if (scope == null) return;
		try {
			String colorStringInterpretation = "0xff0000";
			if (color != null) {
				colorStringInterpretation = color.interpretString(scope);
			}

			int[] rgb = ShowTextUtils.calculateColorRGBs(colorStringInterpretation);
			Color colorInterpretation = new Color();
			Color.argb8888ToColor(colorInterpretation, android.graphics.Color.argb(0xFF, rgb[0], rgb[1], rgb[2]));
			sprite.setEmbroideryThreadColor(colorInterpretation);
		} catch (Exception exception) {
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", exception);
		}
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public void setColor(Formula color) {
		this.color = color;
	}
}
