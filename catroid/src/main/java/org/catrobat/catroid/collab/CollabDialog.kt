package org.catrobat.catroid.collab

import android.graphics.drawable.GradientDrawable
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.catrobat.catroid.R
import org.catrobat.catroid.utils.ToastUtil
import java.io.File

class CollabDialog(
    private val activity: AppCompatActivity,
    private var projectName: String
) {
    private var projectSpinner: Spinner? = null
    private var dialog: AlertDialog? = null
    private var root: LinearLayout? = null
    private var membersBox: LinearLayout? = null
    private var requestsBox: LinearLayout? = null
    private var requestsHeader: TextView? = null
    private var statusView: TextView? = null
    private var shownCode: String? = null

    private var joining = false
    private var members: Map<String, CollabMember> = emptyMap()
    private var requests: Map<String, CollabRequest> = emptyMap()
    private var meta: CollabMeta? = null
    private var lastCode: String = ""
    private var lastSyncStatus: String = ""
    private var syncStatusView: TextView? = null

    fun show() {
        CollabSession.initOnce(activity.applicationContext)
        build()
        dialog?.show()
        attachCallbacks()
        if (!CollabSession.isActive) {
            CollabSession.restoreSession { ok ->
                activity.runOnUiThread {
                    if (ok) {
                        val sid = CollabSession.sessionId
                        if (sid != null) SyncWorker.start(sid, CollabSession.isHost)
                    }
                    refresh()
                }
            }
        } else if (!CollabSession.isHost) {
            SyncWorker.ensureProject()
        }
        refresh()
    }

    private fun dp(value: Int): Int {
        return (value * activity.resources.displayMetrics.density).toInt()
    }

    private fun build() {
        val scroll = ScrollView(activity)
        val container = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(12), dp(20), dp(12))
        }
        scroll.addView(container)
        root = container
        statusView = TextView(activity).apply { container.addView(this) }
        membersBox = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            container.addView(this)
        }
        requestsHeader = TextView(activity).apply {
            text = activity.getString(R.string.collab_requests)
            visibility = View.GONE
            container.addView(this)
        }
        requestsBox = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            container.addView(this)
        }
        dialog = AlertDialog.Builder(activity)
            .setTitle(R.string.collab_title)
            .setView(scroll)
            .setNeutralButton(R.string.collab_close, null)
            .create()
        dialog?.setOnDismissListener {
            CollabSession.onMembersChanged = null
            CollabSession.onRequestsChanged = null
            CollabSession.onMetaChanged = null
            CollabSession.onAccessRevoked = null
            SyncWorker.onStatus = null
            PresenceRenderer.removeObserver(OBSERVER_KEY)
        }
    }

    private fun attachCallbacks() {
        CollabSession.onMembersChanged = { map ->
            activity.runOnUiThread {
                members = map
                refreshLists()
            }
        }
        CollabSession.onRequestsChanged = { map ->
            activity.runOnUiThread {
                requests = map
                refreshLists()
            }
        }
        CollabSession.onMetaChanged = { meta ->
            activity.runOnUiThread {
                val closedChanged = this.meta?.closed != meta.closed
                this.meta = meta
                if (closedChanged) refresh() else refreshLists()
            }
        }
        CollabSession.onAccessRevoked = {
            activity.runOnUiThread {
                SyncWorker.stop()
                refresh()
            }
        }
        SyncWorker.onStatus = { text ->
            activity.runOnUiThread {
                lastSyncStatus = text
                syncStatusView?.text = text
            }
        }
        PresenceRenderer.addObserver(OBSERVER_KEY) {
            activity.runOnUiThread { refreshLists() }
        }
    }

    private fun refresh() {
        val container = root ?: return
        container.removeAllViews()
        statusView = TextView(activity).apply { container.addView(this) }
        if (!CollabSession.isActive) {
            buildInactive(container)
        } else {
            buildActive(container)
        }
        membersBox = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            container.addView(this)
        }
        requestsHeader = TextView(activity).apply {
            text = activity.getString(R.string.collab_requests)
            visibility = View.GONE
            container.addView(this)
        }
        requestsBox = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            container.addView(this)
        }
        refreshLists()
    }

    private fun buildInactive(container: LinearLayout) {
        val root = org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY
        val availableProjects = try {
            org.catrobat.catroid.utils.FileMetaDataExtractor.getProjectNames(root).sorted()
        } catch (e: Exception) {
            emptyList<String>()
        }

        if (availableProjects.isNotEmpty()) {
            container.addView(TextView(activity).apply {
                text = activity.getString(R.string.collab_select_project)
                setPadding(0, 0, 0, dp(4))
            })
            projectSpinner = Spinner(activity).apply {
                adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_dropdown_item, availableProjects)
                setPadding(0, 0, 0, dp(8))
                val defaultIndex = if (projectName.isNotEmpty()) availableProjects.indexOf(projectName) else -1
                if (defaultIndex >= 0) {
                    setSelection(defaultIndex)
                }
                container.addView(this)
            }
        } else if (projectName.isNotEmpty()) {
            container.addView(TextView(activity).apply {
                text = "${activity.getString(R.string.collab_active_project)}: $projectName"
                setTypeface(null, android.graphics.Typeface.BOLD)
                setPadding(0, 0, 0, dp(8))
            })
        }

        val nameInput = EditText(activity).apply {
            hint = activity.getString(R.string.collab_name_hint)
            setText(CollabAuth.savedDisplayName())
            inputType = InputType.TYPE_CLASS_TEXT
            container.addView(this)
        }
        val sessionInput = EditText(activity).apply {
            hint = activity.getString(R.string.collab_session_hint)
            setText(lastCode)
            inputType = InputType.TYPE_CLASS_TEXT
            container.addView(this)
        }
        val row = LinearLayout(activity).apply {
            orientation = LinearLayout.HORIZONTAL
            container.addView(this)
        }
        row.addView(Button(activity).apply {
            text = activity.getString(R.string.collab_create)
            setOnClickListener {
                val name = nameInput.text.toString().trim()
                if (name.isEmpty()) {
                    ToastUtil.showError(activity, R.string.collab_name_hint)
                    return@setOnClickListener
                }
                startCreate(name)
            }
        })
        row.addView(Button(activity).apply {
            text = activity.getString(R.string.collab_join)
            setOnClickListener {
                val name = nameInput.text.toString().trim()
                if (name.isEmpty()) {
                    ToastUtil.showError(activity, R.string.collab_name_hint)
                    return@setOnClickListener
                }
                startJoin(name, sessionInput.text.toString())
            }
        })
        statusView?.text = activity.getString(R.string.collab_offline)
    }

    private fun buildActive(container: LinearLayout) {
        val sid = CollabSession.sessionId ?: ""
        val info = StringBuilder()
        info.append(activity.getString(R.string.collab_session)).append(": ").append(sid)
        info.append("\n").append(activity.getString(R.string.collab_role)).append(": ")
            .append(roleLabel(CollabSession.myRole))
        val currentMeta = meta
        if (currentMeta != null && currentMeta.projectName != projectName) {
            info.append("\n").append(activity.getString(R.string.collab_project_mismatch))
        }
        if (shownCode != null && CollabSession.isHost) {
            info.append("\n").append(activity.getString(R.string.collab_code)).append(": ")
                .append(sid).append("-").append(shownCode)
        }
        statusView?.text = info.toString()
        val row = LinearLayout(activity).apply {
            orientation = LinearLayout.HORIZONTAL
            container.addView(this)
        }
        if (CollabSession.isHost) {
            row.addView(Button(activity).apply {
                text = activity.getString(R.string.collab_new_code)
                setOnClickListener {
                    CollabSession.createInvite(CollabRoles.EDITOR) { code ->
                        activity.runOnUiThread {
                            if (code == null) {
                                ToastUtil.showError(activity, R.string.collab_no_connection)
                            } else {
                                shownCode = code
                                refresh()
                            }
                        }
                    }
                }
            })
            row.addView(Button(activity).apply {
                val closed = meta?.closed == true
                text = activity.getString(if (closed) R.string.collab_open_room else R.string.collab_close_room)
                setOnClickListener { CollabSession.setClosed(!closed) }
            })
        }
        row.addView(Button(activity).apply {
            text = activity.getString(R.string.collab_leave)
            setOnClickListener {
                SyncWorker.stop()
                CollabSession.leave()
                shownCode = null
                refresh()
            }
        })
        if (CollabSession.isHost) {
            buildGitSection(container)
        }
        syncStatusView = TextView(activity).apply {
            text = lastSyncStatus
            container.addView(this)
        }
    }

    private fun buildGitSection(container: LinearLayout) {
        val hasToken = try {
            !org.catrobat.catroid.utils.git.TokenManager.getToken(activity).isNullOrEmpty()
        } catch (e: Exception) {
            false
        }
        val tree = SyncWorker.workTreeDir()
        val repoReady = tree != null && try {
            java.io.File(tree, ".git").isDirectory
        } catch (e: Exception) {
            false
        }
        val statusRow = LinearLayout(activity).apply {
            orientation = LinearLayout.HORIZONTAL
            container.addView(this)
        }
        statusRow.addView(TextView(activity).apply {
            text = activity.getString(R.string.collab_git_token_hint) + ": " +
                if (hasToken) "OK" else activity.getString(R.string.collab_git_no_token)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        })
        if (hasToken) {
            statusRow.addView(Button(activity).apply {
                text = activity.getString(R.string.collab_git_clear)
                setOnClickListener {
                    try {
                        org.catrobat.catroid.utils.git.TokenManager.clearToken(activity)
                    } catch (e: Exception) {
                    }
                    refresh()
                }
            })
        }

        val actionRow = LinearLayout(activity).apply {
            orientation = LinearLayout.HORIZONTAL
            container.addView(this)
        }
        actionRow.addView(Button(activity).apply {
            text = activity.getString(R.string.collab_git_get_pat)
            setOnClickListener {
                val url = "https://github.com/settings/tokens/new?scopes=repo&description=NeoCatroid"
                try {
                    activity.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url)))
                } catch (e: Exception) {
                    ToastUtil.showError(activity, R.string.error_internet_connection)
                }
            }
        })
        val clientId = org.catrobat.catroid.BuildConfig.GITHUB_CLIENT_ID
        if (!clientId.isNullOrEmpty()) {
            actionRow.addView(Button(activity).apply {
                text = activity.getString(R.string.collab_git_login_oauth)
                setOnClickListener {
                    val authUrl = "https://github.com/login/oauth/authorize?client_id=${clientId}&scope=repo&redirect_uri=NeoCatroid://github-callback"
                    try {
                        activity.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(authUrl)))
                    } catch (e: Exception) {
                        ToastUtil.showError(activity, R.string.error_internet_connection)
                    }
                }
            })
        }

        val tokenRow = LinearLayout(activity).apply {
            orientation = LinearLayout.HORIZONTAL
            container.addView(this)
        }
        val tokenInput = EditText(activity).apply {
            hint = activity.getString(R.string.collab_git_token_hint)
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            tokenRow.addView(this)
        }
        tokenRow.addView(Button(activity).apply {
            text = activity.getString(R.string.collab_git_save)
            setOnClickListener {
                val value = tokenInput.text.toString().trim()
                if (value.isEmpty()) return@setOnClickListener
                Thread {
                    try {
                        org.catrobat.catroid.utils.git.TokenManager.saveToken(activity, value)
                    } catch (e: Exception) {
                    }
                    activity.runOnUiThread { refresh() }
                }.start()
            }
        })
        val repoRow = LinearLayout(activity).apply {
            orientation = LinearLayout.HORIZONTAL
            container.addView(this)
        }
        repoRow.addView(TextView(activity).apply {
            text = if (repoReady) activity.getString(R.string.collab_git_ready) else activity.getString(R.string.collab_git_no_token)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        })
        repoRow.addView(Button(activity).apply {
            text = activity.getString(R.string.collab_git_init)
            setOnClickListener { initRepo() }
        })
    }

    private fun initRepo() {
        Thread {
            try {
                val token = org.catrobat.catroid.utils.git.TokenManager.getToken(activity)
                if (token.isNullOrEmpty()) {
                    activity.runOnUiThread {
                        ToastUtil.showError(activity, R.string.collab_git_no_token)
                    }
                    return@Thread
                }
                val project = org.catrobat.catroid.ProjectManager.getInstance().currentProject
                val tree = SyncWorker.workTreeDir()
                if (project?.directory == null || tree == null) {
                    activity.runOnUiThread {
                        ToastUtil.showError(activity, R.string.collab_no_connection)
                    }
                    return@Thread
                }
                val files = SyncWorker.files() ?: return@Thread
                val codeXml = files.readCodeXml() ?: return@Thread
                SyncEngine.syncWorkTree(project.directory, tree,
                    SyncEngine.manifestOf(files), codeXml)
                val result = org.catrobat.catroid.utils.git.GitController(tree)
                    .initializeAndPushNewRepository(token, project.name + "-collab", true)
                activity.runOnUiThread {
                    if (result is org.catrobat.catroid.utils.git.GitResult.Success) {
                        ToastUtil.showSuccess(activity, R.string.collab_git_ready)
                    } else {
                        ToastUtil.showError(activity, R.string.collab_no_connection)
                    }
                    refresh()
                }
            } catch (e: Exception) {
                activity.runOnUiThread {
                    ToastUtil.showError(activity, R.string.collab_no_connection)
                }
            }
        }.start()
    }

    private fun startCreate(name: String) {
        val selected = (projectSpinner?.selectedItem as? String).orEmpty()
        val targetProject = if (selected.isNotEmpty()) selected else projectName
        if (targetProject.isEmpty()) {
            ToastUtil.showError(activity, R.string.collab_project_mismatch)
            return
        }
        projectName = targetProject

        CollabAuth.saveDisplayName(name)
        val hue = PresenceColors.hueFor(emptyList())
        PresenceRenderer.myHue = hue
        PresenceRenderer.myName = name
        statusView?.text = activity.getString(R.string.collab_connecting)
        Thread {
            val root = org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY
            val curProj = org.catrobat.catroid.ProjectManager.getInstance().currentProject
            if (curProj == null || curProj.name != targetProject) {
                val encoded = org.catrobat.catroid.utils.FileMetaDataExtractor.encodeSpecialCharsForFileSystem(targetProject)
                var dir = File(root, encoded)
                if (!dir.isDirectory) {
                    dir = root.listFiles()?.firstOrNull { f ->
                        f.isDirectory && try {
                            val meta = org.catrobat.catroid.content.backwardcompatibility.ProjectMetaDataParser(File(f, org.catrobat.catroid.common.Constants.CODE_XML_FILE_NAME)).projectMetaData
                            meta.name == targetProject
                        } catch (e: Exception) { false }
                    } ?: dir
                }
                if (!dir.isDirectory || !org.catrobat.catroid.io.asynctask.loadProject(dir, activity)) {
                    activity.runOnUiThread {
                        ToastUtil.showError(activity, R.string.collab_project_mismatch)
                        refresh()
                    }
                    return@Thread
                }
            }
            activity.runOnUiThread {
                CollabSession.createSession(projectName, name, hue) { sid, code ->
                    activity.runOnUiThread {
                        if (sid == null) {
                            ToastUtil.showError(activity, R.string.collab_no_connection)
                            refresh()
                        } else {
                            shownCode = code
                            attachCallbacks()
                            CollabSession.startListeners()
                            SyncWorker.start(sid, true)
                            refresh()
                        }
                    }
                }
            }
        }.start()
    }

    private fun startJoin(name: String, raw: String) {
        val parts = raw.trim().uppercase().split("-")
        if (parts.size != 2 || !CollabCodes.isValidSessionId(parts[0]) || !CollabCodes.isValidInviteCode(parts[1])) {
            ToastUtil.showError(activity, R.string.collab_bad_code)
            return
        }
        if (joining) return
        joining = true
        lastCode = raw.trim().uppercase()
        CollabAuth.saveDisplayName(name)
        val hue = PresenceColors.hueFor(emptyList())
        PresenceRenderer.myHue = hue
        PresenceRenderer.myName = name
        statusView?.text = activity.getString(R.string.collab_connecting)
        CollabSession.claimInvite(parts[0], parts[1], name, hue) { ok ->
            activity.runOnUiThread {
                if (!ok) {
                    joining = false
                    ToastUtil.showError(activity, R.string.collab_bad_code)
                    refresh()
                    return@runOnUiThread
                }
                CollabSession.awaitApproval(90000L) { role ->
                    activity.runOnUiThread {
                        joining = false
                        if (role == null) {
                            ToastUtil.showError(activity, R.string.collab_not_approved)
                            CollabSession.leave()
                        } else {
                            ToastUtil.showSuccess(activity, R.string.collab_joined)
                            attachCallbacks()
                            val joinedSid = CollabSession.sessionId
                            if (joinedSid != null) SyncWorker.start(joinedSid, false)
                        }
                        refresh()
                    }
                }
            }
        }
    }

    private fun refreshLists() {
        if (dialog?.isShowing != true) return
        membersBox?.removeAllViews()
        requestsBox?.removeAllViews()
        val box = membersBox ?: return
        box.addView(TextView(activity).apply { text = activity.getString(R.string.collab_members) })
        addMemberRow(box, PresenceColors.colorInt(PresenceRenderer.myHue), memberTitle(PresenceRenderer.myName, null, true), null)
        val myUid = CollabSession.myUid
        for ((uid, member) in members.entries.sortedBy { it.value.name }) {
            if (uid == myUid) continue
            addMemberRow(
                box,
                PresenceColors.colorInt(member.colorHue),
                memberTitle(member.name, member.role, false) + whereSuffix(uid),
                if (CollabSession.isHost) uid else null
            )
        }
        val reqBox = requestsBox ?: return
        val isHost = CollabSession.isHost
        requestsHeader?.visibility = if (isHost && requests.isNotEmpty()) View.VISIBLE else View.GONE
        if (!isHost) return
        for ((uid, req) in requests) {
            val line = LinearLayout(activity).apply { orientation = LinearLayout.HORIZONTAL }
            line.addView(TextView(activity).apply {
                text = req.name
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            })
            line.addView(Button(activity).apply {
                text = activity.getString(R.string.collab_approve_editor)
                setOnClickListener { approveWithDistinctHue(uid, req, CollabRoles.EDITOR) }
            })
            line.addView(Button(activity).apply {
                text = activity.getString(R.string.collab_approve_viewer)
                setOnClickListener { approveWithDistinctHue(uid, req, CollabRoles.VIEWER) }
            })
            line.addView(Button(activity).apply {
                text = "×"
                setOnClickListener { CollabSession.rejectRequest(uid) }
            })
            reqBox.addView(line)
        }
    }

    private fun approveWithDistinctHue(uid: String, req: CollabRequest, role: String) {
        val taken = members.values.map { it.colorHue }
        val hue = if (taken.isEmpty()) req.colorHue else PresenceColors.hueFor(taken)
        CollabSession.approveRequest(uid, req.copy(colorHue = hue), role) { ok ->
            if (!ok) {
                activity.runOnUiThread {
                    ToastUtil.showError(activity, R.string.collab_no_connection)
                }
            }
        }
    }

    private fun memberTitle(name: String, role: String?, isSelf: Boolean): String {
        val label = StringBuilder(if (name.isEmpty()) "?" else name)
        if (isSelf) {
            label.append(" (").append(activity.getString(R.string.collab_you)).append(")")
        } else if (role != null) {
            label.append(" · ").append(roleLabel(role))
        }
        return label.toString()
    }

    private fun whereSuffix(uid: String): String {
        val where = PresenceRenderer.whereFor(uid)
        return if (where.isEmpty()) "" else " → $where"
    }

    private fun addMemberRow(box: LinearLayout, color: Int, title: String, kickUid: String?) {
        val line = LinearLayout(activity).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        line.addView(View(activity).apply {
            val size = dp(14)
            layoutParams = LinearLayout.LayoutParams(size, size).apply {
                marginEnd = dp(8)
            }
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(color)
            }
        })
        line.addView(TextView(activity).apply {
            text = title
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        })
        if (kickUid != null) {
            line.addView(Button(activity).apply {
                text = "×"
                setOnClickListener { CollabSession.kick(kickUid) }
            })
        }
        box.addView(line)
    }

    private fun roleLabel(role: String): String {
        return when (role) {
            CollabRoles.HOST -> activity.getString(R.string.collab_role_host)
            CollabRoles.EDITOR -> activity.getString(R.string.collab_role_editor)
            else -> activity.getString(R.string.collab_role_viewer)
        }
    }

    companion object {
        private const val OBSERVER_KEY = "collab_dialog"
    }
}
