package org.catrobat.catroid.collab

import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.catrobat.catroid.CatroidApplication

object CollabFirebase {
    private const val TAG = "CollabFirebase"
    const val APP_NAME = "collab"

    private const val APPLICATION_ID = "1:910379908682:android:27ae2aa6d6fa5f2a2270b3"
    private const val API_KEY = "AIzaSyCtR88-Jlj-7Vm63g6dBA7lmgp3xmmudZY"
    private const val PROJECT_ID = "privacy-neocatroid"
    private const val SENDER_ID = "910379908682"

    @Volatile private var app: FirebaseApp? = null

    fun app(): FirebaseApp? {
        app?.let { return it }
        return try {
            val context = CatroidApplication.getAppContext()
            val existing = FirebaseApp.getApps(context).firstOrNull { it.name == APP_NAME }
            if (existing != null) {
                app = existing
                return existing
            }
            val options = FirebaseOptions.Builder()
                .setApplicationId(APPLICATION_ID)
                .setApiKey(API_KEY)
                .setProjectId(PROJECT_ID)
                .setGcmSenderId(SENDER_ID)
                .build()
            FirebaseApp.initializeApp(context, options, APP_NAME)?.also { app = it }
        } catch (e: Exception) {
            Log.w(TAG, "collab firebase app init failed", e)
            null
        }
    }

    fun firestore(): FirebaseFirestore? {
        return try {
            app()?.let { FirebaseFirestore.getInstance(it) }
        } catch (e: Exception) {
            Log.w(TAG, "collab firestore unavailable", e)
            null
        }
    }

    fun auth(): FirebaseAuth? {
        return try {
            app()?.let { FirebaseAuth.getInstance(it) }
        } catch (e: Exception) {
            Log.w(TAG, "collab auth unavailable", e)
            null
        }
    }
}
