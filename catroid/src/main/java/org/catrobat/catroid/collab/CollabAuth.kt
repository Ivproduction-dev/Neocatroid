package org.catrobat.catroid.collab

import android.content.Context
import android.util.Log
import org.catrobat.catroid.CatroidApplication

object CollabAuth {
    private const val PREFS = "collab_prefs"
    private const val KEY_NAME = "display_name"

    fun ensureSignedIn(callback: (String?) -> Unit) {
        try {
            val auth = CollabFirebase.auth()
            if (auth == null) {
                callback(null)
                return
            }
            val current = auth.currentUser?.uid.orEmpty()
            if (current.isNotEmpty()) {
                callback(current)
                return
            }
            auth.signInAnonymously()
                .addOnSuccessListener { callback(auth.currentUser?.uid?.takeIf { it.isNotEmpty() }) }
                .addOnFailureListener { error ->
                    Log.w("CollabAuth", "anonymous sign-in failed: ${error.message}")
                    callback(null)
                }
        } catch (e: Exception) {
            callback(null)
        }
    }

    fun savedDisplayName(): String {
        return try {
            CatroidApplication.getAppContext()
                .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(KEY_NAME, "").orEmpty()
        } catch (e: Exception) {
            ""
        }
    }

    fun saveDisplayName(name: String) {
        try {
            CatroidApplication.getAppContext()
                .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putString(KEY_NAME, name).apply()
        } catch (e: Exception) {
        }
    }
}
