package com.example.e_comm

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.e_comm.databinding.ActivitySettingsBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!ECommApp.isFirebaseAvailable(this)) {
            Toast.makeText(this, "Settings are unavailable. Missing google-services.json.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        try {
            binding = ActivitySettingsBinding.inflate(layoutInflater)
            setContentView(binding.root)
            auth = FirebaseAuth.getInstance()
        } catch (t: Throwable) {
            Toast.makeText(this, "Application initialization failed.", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        prefs = getSharedPreferences("app_settings", MODE_PRIVATE)

        loadCurrentSettings()

        binding.btnSaveSettings.setOnClickListener { saveSettings() }
        binding.btnLogout.setOnClickListener { logout() }
    }

    private fun loadCurrentSettings() {
        val user = auth.currentUser
        binding.etSettingsName.setText(user?.displayName ?: "")
        binding.etSettingsEmail.setText(user?.email ?: "")
        binding.switchNotifications.isChecked = prefs.getBoolean("notifications_enabled", true)
    }

    private fun saveSettings() {
        val newName = binding.etSettingsName.text.toString().trim()
        val newEmail = binding.etSettingsEmail.text.toString().trim()
        val currentPassword = binding.etCurrentPassword.text.toString()
        val newPassword = binding.etNewPassword.text.toString()
        val notificationsEnabled = binding.switchNotifications.isChecked

        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "You must be logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Notification preference is stored locally - save it regardless of other changes
        prefs.edit { putBoolean("notifications_enabled", notificationsEnabled) }

        val emailChanged = newEmail.isNotEmpty() && newEmail != user.email
        val passwordChanged = newPassword.isNotEmpty()

        // Display name can be updated without re-authentication
        if (newName.isNotEmpty() && newName != user.displayName) {
            val profileUpdate = UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build()
            user.updateProfile(profileUpdate)
        }

        if (!emailChanged && !passwordChanged) {
            Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show()
            return
        }

        // Email or password changes require re-authentication with the current password
        if (currentPassword.isEmpty()) {
            Toast.makeText(
                this,
                "Enter your current password to change email or password",
                Toast.LENGTH_LONG,
            ).show()
            return
        }

        val credential = EmailAuthProvider.getCredential(user.email ?: "", currentPassword)
        user.reauthenticate(credential)
            .addOnSuccessListener {
                if (emailChanged) updateEmail(newEmail)
                if (passwordChanged) updatePassword(newPassword)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateEmail(newEmail: String) {
        auth.currentUser?.verifyBeforeUpdateEmail(newEmail)
            ?.addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Verification link sent to $newEmail",
                    Toast.LENGTH_LONG
                ).show()
            }
            ?.addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update email: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun updatePassword(newPassword: String) {
        auth.currentUser?.updatePassword(newPassword)
            ?.addOnSuccessListener {
                binding.etCurrentPassword.text?.clear()
                binding.etNewPassword.text?.clear()
                Toast.makeText(this, "Password updated", Toast.LENGTH_SHORT).show()
            }
            ?.addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update password: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun logout() {
        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
