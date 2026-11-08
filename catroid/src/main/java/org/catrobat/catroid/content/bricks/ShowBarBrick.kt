package org.catrobat.catroid.content.bricks

import android.content.Context
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Nameable
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.ScriptSequenceAction
import org.catrobat.catroid.content.actions.ShowBarAction
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner
import org.catrobat.catroid.content.bricks.brickspinner.NewOption
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.ui.UiUtils

class ShowBarBrick(
    private var userVariable: UserVariable? = null
) : FormulaBrick(), UserVariableBrickInterface {

    private var autoUpdate: Boolean = true
    private var pinned: Boolean = true

    @Transient
    private var spinner: BrickSpinner<UserVariable>? = null

    init {
        registerFormulaFields()
    }

    private fun registerFormulaFields() {
        addAllowedBrickField(Brick.BrickField.TEXTCOLOR, R.id.brick_show_bar_fill_edit)
        addAllowedBrickField(Brick.BrickField.BGCOLOR, R.id.brick_show_bar_track_edit)
        addAllowedBrickField(Brick.BrickField.WIDTH, R.id.brick_show_bar_width_edit)
        addAllowedBrickField(Brick.BrickField.HEIGHT, R.id.brick_show_bar_height_edit)
        addAllowedBrickField(Brick.BrickField.VALUE_1, R.id.brick_show_bar_min_edit)
        addAllowedBrickField(Brick.BrickField.VALUE_2, R.id.brick_show_bar_max_edit)
        addAllowedBrickField(Brick.BrickField.X_POSITION, R.id.brick_show_bar_x_edit)
        addAllowedBrickField(Brick.BrickField.Y_POSITION, R.id.brick_show_bar_y_edit)
    }

    override fun getUserVariable(): UserVariable? = userVariable

    override fun setUserVariable(variable: UserVariable?) {
        this.userVariable = variable
    }

    fun getSpinnerId(): Int = R.id.brick_show_bar_variable_spinner

    override fun getViewResource(): Int = R.layout.brick_show_bar

    override fun getView(context: Context): View {
        super.getView(context)
        val root = view

        val sprite = ProjectManager.getInstance().currentSprite
        val items = ArrayList<Nameable>()
        items.add(NewOption(context.getString(R.string.new_option)))
        sprite?.getUserVariables()?.let { items.addAll(it) }
        ProjectManager.getInstance().currentlyEditedScene?.getSceneVariables()?.let { items.addAll(it) }
        ProjectManager.getInstance().currentProject?.getUserVariables()?.let { items.addAll(it) }

        spinner = BrickSpinner(getSpinnerId(), root, items)
        spinner?.setOnItemSelectedListener(this)
        spinner?.setSelection(userVariable)

        val autoSpinner = root?.findViewById<Spinner>(R.id.brick_show_bar_auto_spinner)
        val pinnedSpinner = root?.findViewById<Spinner>(R.id.brick_show_bar_pinned_spinner)
        val options = arrayOf(
            context.getString(R.string.brick_bar_no),
            context.getString(R.string.brick_bar_yes)
        )
        autoSpinner?.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, options)
        pinnedSpinner?.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, options)
        autoSpinner?.setSelection(if (autoUpdate) 1 else 0)
        pinnedSpinner?.setSelection(if (pinned) 1 else 0)
        autoSpinner?.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, v: View?, pos: Int, id: Long) {
                autoUpdate = pos == 1
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
        }
        pinnedSpinner?.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, v: View?, pos: Int, id: Long) {
                pinned = pos == 1
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
        }
        return root!!
    }

    override fun onNewOptionSelected(spinnerId: Int?) {
        val activity = UiUtils.getActivityFromView(view) ?: return
        val currentProject = ProjectManager.getInstance().currentProject
        val currentSprite = ProjectManager.getInstance().currentSprite
        val builder = org.catrobat.catroid.content.bricks.brickspinner.UserVariableBrickTextInputDialogBuilder(
            currentProject, currentSprite, userVariable, activity, spinner
        )
        builder.show()
    }

    override fun onEditOptionSelected(spinnerId: Int?) = Unit

    override fun onStringOptionSelected(spinnerId: Int?, string: String?) = Unit

    override fun onItemSelected(spinnerId: Int?, item: UserVariable?) {
        userVariable = item
    }

    override fun addActionToSequence(sprite: Sprite, sequence: ScriptSequenceAction) {
        val action = ShowBarAction()
        action.scope = Scope(ProjectManager.getInstance().currentProject, sprite, sequence)
        action.variable = getUserVariable()
        action.fillColor = getFormulaWithBrickField(Brick.BrickField.TEXTCOLOR, true)
        action.trackColor = getFormulaWithBrickField(Brick.BrickField.BGCOLOR, true)
        action.widthPct = getFormulaWithBrickField(Brick.BrickField.WIDTH, true)
        action.heightPct = getFormulaWithBrickField(Brick.BrickField.HEIGHT, true)
        action.minValue = getFormulaWithBrickField(Brick.BrickField.VALUE_1, true)
        action.maxValue = getFormulaWithBrickField(Brick.BrickField.VALUE_2, true)
        action.xPct = getFormulaWithBrickField(Brick.BrickField.X_POSITION, true)
        action.yPct = getFormulaWithBrickField(Brick.BrickField.Y_POSITION, true)
        action.autoUpdate = autoUpdate
        action.pinned = pinned
        sequence.addAction(action)
    }

    fun setFillColor(formula: Formula) = setFormulaWithBrickField(Brick.BrickField.TEXTCOLOR, formula)

    fun setTrackColor(formula: Formula) = setFormulaWithBrickField(Brick.BrickField.BGCOLOR, formula)

    fun setWidthPct(formula: Formula) = setFormulaWithBrickField(Brick.BrickField.WIDTH, formula)

    fun setHeightPct(formula: Formula) = setFormulaWithBrickField(Brick.BrickField.HEIGHT, formula)

    fun setMinValue(formula: Formula) = setFormulaWithBrickField(Brick.BrickField.VALUE_1, formula)

    fun setMaxValue(formula: Formula) = setFormulaWithBrickField(Brick.BrickField.VALUE_2, formula)

    fun setXPct(formula: Formula) = setFormulaWithBrickField(Brick.BrickField.X_POSITION, formula)

    fun setYPct(formula: Formula) = setFormulaWithBrickField(Brick.BrickField.Y_POSITION, formula)

    fun setAutoUpdate(value: Boolean) {
        autoUpdate = value
    }

    fun setPinned(value: Boolean) {
        pinned = value
    }

    override fun clone(): Brick {
        val clone = super.clone() as ShowBarBrick
        clone.spinner = null
        return clone
    }
}
