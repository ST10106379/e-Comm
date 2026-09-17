package com.example.e_comm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.e_comm.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Safety check: Ensure Firebase is available before doing anything else
        if (!ECommApp.isFirebaseAvailable(this)) {
            Log.e("LoginActivity", "Firebase unavailable - finishing")
            Toast.makeText(this, "Authentication is unavailable. Missing google-services.json.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        try {
            binding = ActivityLoginBinding.inflate(layoutInflater)
            setContentView(binding.root)
            auth = FirebaseAuth.getInstance()
        } catch (t: Throwable) {
            Log.e("LoginActivity", "Initialization error", t)
            Toast.makeText(this, "Application initialization failed.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        binding.btnLogin.setOnClickListener { 
            if (::auth.isInitialized) {
                attemptLogin()
            } else {
                Toast.makeText(this, "Login unavailable", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvGoToSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        // Skip login if already signed in
        if (::auth.isInitialized && auth.currentUser != null) {
            startActivity(Intent(this, ProductListActivity::class.java))
            finish()
        }
    }

    private fun attemptLogin() {
        val email = binding.etLoginEmail.text.toString().trim()
        val password = binding.etLoginPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                startActivity(Intent(this, ProductListActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Login failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
