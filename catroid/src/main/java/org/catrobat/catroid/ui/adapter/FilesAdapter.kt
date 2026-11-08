
package org.catrobat.catroid.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.danvexteam.lunoscript_annotations.LunoClass
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project

@LunoClass
class FilesAdapter(
    private val project: Project?,
    private val files: List<String>,
    private val onDelete: (String) -> Unit,
    private val onCopy: (String) -> Unit,
    private val onOpen: (String) -> Unit,
    private val onRename: (String) -> Unit
) : RecyclerView.Adapter<FilesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fileName: TextView = view.findViewById(R.id.file_name)
        val fileSize: TextView = view.findViewById(R.id.file_size)
        val fileIcon: ImageView = view.findViewById(R.id.file_icon)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteButton)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fileName = files[position]
        holder.fileName.text = fileName

        val file = project?.getFile(fileName) ?: project?.getLib(fileName)
        holder.deleteButton.visibility = View.VISIBLE

        if (file?.isDirectory == true) {
            holder.fileIcon.setImageResource(R.drawable.folder_copy_24px)
            val itemsCount = file.listFiles()?.size ?: 0
            holder.fileSize.text = holder.itemView.context.getString(R.string.project_files_elements_count, itemsCount)
        } else {
            val extension = fileName.substringAfterLast('.', "").lowercase()
            val iconRes = when (extension) {
                "py", "lua", "js", "java", "kt", "xml", "json", "txt", "md" -> R.drawable.code_24px
                "png", "jpg", "jpeg", "webp" -> R.drawable.ic_draw_image
                "mp3", "wav", "ogg" -> R.drawable.ic_music_library
                "rscene", "glb", "obj" -> R.drawable.deployed_code_24px
                else -> R.drawable.file_present_24px
            }
            holder.fileIcon.setImageResource(iconRes)
            holder.fileSize.text = if (file?.exists() == true) {
                formatFileSize(file.length())
            } else {
                "Файл не найден"
            }
        }

        val density = holder.itemView.context.resources.displayMetrics.density
        val locker = org.catrobat.catroid.collab.ScriptLockManager.projectFileLockerOf(fileName)

        if (locker != null) {
            holder.itemView.alpha = 0.72f
            val color = org.catrobat.catroid.collab.PresenceColors.colorInt(locker.colorHue)
            holder.itemView.foreground = org.catrobat.catroid.collab.PresenceBorderDrawable(listOf(color), density)
        } else if (org.catrobat.catroid.collab.ScriptLockManager.isProjectFileHeldByMe(fileName)) {
            holder.itemView.alpha = 1.0f
            val myColor = org.catrobat.catroid.collab.PresenceColors.colorInt(org.catrobat.catroid.collab.PresenceRenderer.myHue)
            holder.itemView.foreground = org.catrobat.catroid.collab.PresenceBorderDrawable(listOf(myColor), density)
        } else {
            holder.itemView.alpha = 1.0f
            if (holder.itemView.foreground is org.catrobat.catroid.collab.PresenceBorderDrawable) {
                holder.itemView.foreground = null
            }
        }

        holder.deleteButton.setOnClickListener {
            val fileLocker = org.catrobat.catroid.collab.ScriptLockManager.projectFileLockerOf(fileName)
            if (fileLocker != null) {
                val context = holder.itemView.context
                org.catrobat.catroid.utils.ToastUtil.showError(
                    context,
                    context.getString(R.string.collab_locked_by, fileLocker.name)
                )
                return@setOnClickListener
            }
            onDelete(fileName)
        }

        holder.itemView.setOnClickListener { view ->
            val popup = androidx.appcompat.widget.PopupMenu(view.context, view)
            popup.menu.add(0, 1, 0, view.context.getString(R.string.project_files_menu_rename))
            popup.menu.add(0, 2, 1, view.context.getString(R.string.project_files_menu_open_code))
            popup.menu.add(0, 3, 2, view.context.getString(R.string.project_files_menu_copy_name))
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    1 -> onRename(fileName)
                    2 -> onOpen(fileName)
                    3 -> onCopy(fileName)
                }
                true
            }
            popup.show()
        }

        holder.itemView.setOnLongClickListener {
            onCopy(fileName)
            true
        }
    }

    fun formatFileSize(size: Long): String {
        val units = arrayOf("Б", "КБ", "МБ", "ГБ", "ТБ")
        var sizeInUnits = size.toDouble()
        var index = 0

        while (sizeInUnits >= 1024 && index < units.size - 1) {
            sizeInUnits /= 1024
            index++
        }

        return String.format("%.1f %s", sizeInUnits, units[index])
    }


    override fun getItemCount() = files.size
}
