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
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.UserVariable;

public class ListenServerBrick extends UserVariableBrickWithFormula {

    private static final long serialVersionUID = 1L;

    private int listenModeSelection;

    public ListenServerBrick() {

    }

    public ListenServerBrick(UserVariable userVariable) {
        this();
        this.userVariable = userVariable;
    }

    public int getListenModeSelection() {
        return listenModeSelection;
    }

    public void setListenModeSelection(int listenModeSelection) {
        this.listenModeSelection = listenModeSelection;
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_listen_server;
    }

    @Override
    public View getView(Context context) {
        View view = super.getView(context);
        android.widget.Spinner modeSpinner = view.findViewById(R.id.listen_server_mode_spinner);
        if (modeSpinner != null) {
            android.widget.ArrayAdapter<CharSequence> modeAdapter =
                    android.widget.ArrayAdapter.createFromResource(context,
                            R.array.listen_tcp_modes, android.R.layout.simple_spinner_item);
            modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            modeSpinner.setAdapter(modeAdapter);
            modeSpinner.setOnItemSelectedListener(
                    new android.widget.AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(android.widget.AdapterView<?> parent,
                                View selectedView, int position, long id) {
                            listenModeSelection = position;
                        }

                        @Override
                        public void onNothingSelected(android.widget.AdapterView<?> parent) {
                        }
                    });
            modeSpinner.setSelection(listenModeSelection);
        }
        return view;
    }

    @Override
    protected int getSpinnerId() {
        return R.id.listen_server_spinner;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory().createListenServerAction(sprite, sequence,
                userVariable, listenModeSelection));
    }
}
