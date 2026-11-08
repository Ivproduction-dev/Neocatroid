package org.catrobat.catroid.content.bricks

import android.content.Context
import android.view.View
import android.widget.CheckBox
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.actions.ScriptSequenceAction
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.HideBarAction
import org.catrobat.catroid.formulaeditor.UserVariable

class HideBarBrick : UserVariableBrick {

    private var hideAll: Boolean = false

    constructor() : super()

    constructor(variable: UserVariable?) : super() {
        setUserVariable(variable)
    }

    override fun getSpinnerId(): Int = R.id.brick_hide_bar_variable_spinner

    override fun getViewResource(): Int = R.layout.brick_hide_bar

    override fun getView(context: Context): View {
        super.getView(context)
        view?.findViewById<CheckBox>(R.id.brick_hide_bar_all_check)?.apply {
            isChecked = hideAll
            setOnCheckedChangeListener { _, checked -> hideAll = checked }
        }
        return view!!
    }

    override fun addActionToSequence(sprite: Sprite, sequence: ScriptSequenceAction) {
        val action = HideBarAction()
        action.scope = Scope(ProjectManager.getInstance().currentProject, sprite, sequence)
        action.variable = getUserVariable()
        action.hideAll = hideAll
        sequence.addAction(action)
    }
}
