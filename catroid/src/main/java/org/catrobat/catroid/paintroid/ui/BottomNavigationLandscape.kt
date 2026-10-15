/*
 * Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
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
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.paintroid.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.catrobat.catroid.R

import org.catrobat.catroid.paintroid.contract.MainActivityContracts.BottomNavigationAppearance
import org.catrobat.catroid.paintroid.tools.ToolType

class BottomNavigationLandscape(
    context: Context,
    private val bottomNavigationView: BottomNavigationView,
    var isUi2: Boolean = false
) : BottomNavigationAppearance {
    private val bottomNavigationMenuView: BottomNavigationMenuView = bottomNavigationView.getChildAt(0) as BottomNavigationMenuView

    init {
        setAppearance(context)
    }

    override fun showCurrentTool(toolType: ToolType) {
        val item = findItemView(R.id.action_current_tool) ?: return
        val icon = item.findViewById<ImageView>(R.id.icon)
        val title = item.findViewById<TextView>(R.id.title)
        icon.setImageResource(Ui2Helper.getToolIcon(toolType, isUi2))
        title.setText(toolType.nameResource)
    }

    fun refreshItems() {
        val menu = bottomNavigationView.menu
        for (i in 0 until bottomNavigationMenuView.childCount) {
            val child = bottomNavigationMenuView.getChildAt(i)
            if (child.id == R.id.action_current_tool) {
                continue
            }
            val menuItem = menu.findItem(child.id) ?: continue
            val iconView = child.findViewById<ImageView>(R.id.icon) ?: continue
            if (child.id == R.id.action_layers && isUi2) {
                iconView.setImageResource(R.drawable.ic_pocketpaint_v2_layers)
            } else {
                iconView.setImageDrawable(menuItem.icon)
            }
            child.findViewById<TextView>(R.id.title)?.text = menuItem.title
        }
    }

    private fun findItemView(@IdRes id: Int): View? {
        for (i in 0 until bottomNavigationMenuView.childCount) {
            val child = bottomNavigationMenuView.getChildAt(i)
            if (child.id == id) {
                return child
            }
        }
        return null
    }

    private fun setAppearance(context: Context) {
        val inflater = LayoutInflater.from(context)
        val menu = bottomNavigationView.menu
        for (i in 0 until bottomNavigationMenuView.childCount) {
            val item = bottomNavigationMenuView.getChildAt(i) as? BottomNavigationItemView ?: continue
            val menuItem = menu.findItem(item.id) ?: continue
            val itemBottomNavigation = inflater.inflate(R.layout.pocketpaint_layout_bottom_navigation_item, bottomNavigationMenuView, false)
            val icon = itemBottomNavigation.findViewById<ImageView>(R.id.icon)
            val text = itemBottomNavigation.findViewById<TextView>(R.id.title)
            if (item.id == R.id.action_layers && isUi2) {
                icon.setImageResource(R.drawable.ic_pocketpaint_v2_layers)
            } else {
                icon.setImageDrawable(menuItem.icon)
            }
            icon.setColorFilter(ContextCompat.getColor(context, R.color.pocketpaint_welcome_dot_active))
            text.text = menuItem.title
            item.removeAllViews()
            item.addView(itemBottomNavigation)
        }
    }
}
