package com.example.e_comm

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class ECommApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeFirebase()
    }

    private fun initializeFirebase() {
        try {
            // Check if google-services.json was processed by the plugin
            val resId = resources.getIdentifier("google_app_id", "string", packageName)
            if (resId == 0) {
                Log.e("ECommApp", "Firebase config (google-services.json) is missing. Firebase features will be disabled.")
                return
            }

            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
            }
            Log.i("ECommApp", "Firebase initialized successfully.")
        } catch (t: Throwable) {
            Log.e("ECommApp", "Failed to initialize Firebase: ${t.message}")
        }
    }

    companion object {
        fun isFirebaseAvailable(context: android.content.Context): Boolean {
            return try {
                val resId = context.resources.getIdentifier("google_app_id", "string", context.packageName)
                if (resId == 0) return false
                
                FirebaseApp.getInstance()
                true
            } catch (_: IllegalStateException) {
                // Not initialized, try one last time
                try {
                    FirebaseApp.initializeApp(context) != null
                } catch (t: Throwable) {
                    false
                }
            } catch (t: Throwable) {
                false
            }
        }
    }
}
