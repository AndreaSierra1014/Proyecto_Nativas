package com.techstore.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.techstore.app.R
import com.techstore.app.data.local.SessionManager
import com.techstore.app.data.repository.AuthRepository
import com.techstore.app.ui.main.MainActivity
import com.techstore.app.util.showBiometricPrompt
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var authRepository: AuthRepository
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        authRepository = AuthRepository()
        sessionManager = SessionManager(this)

        val biometricButton = findViewById<MaterialButton>(R.id.btnBiometric)
        biometricButton.visibility =
            if (sessionManager.biometricEnabled && Firebase.auth.currentUser != null) {
                View.VISIBLE
            } else {
                View.GONE
            }

        findViewById<MaterialButton>(R.id.btnLogin).setOnClickListener { login() }
        biometricButton.setOnClickListener {
            showBiometricPrompt(
                context = this,
                title = "Iniciar sesión con biometría",
                subtitle = "Usa la biometría configurada en tu dispositivo",
                negativeButtonText = "Cancelar",
                onAuthSuccess = {
                    val user = Firebase.auth.currentUser
                    if (sessionManager.biometricEnabled && user != null) {
                        sessionManager.uid = user.uid
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Inicia sesión con tu correo y contraseña.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                onAuthError = { error ->
                    Toast.makeText(this, "Error biométrico: $error", Toast.LENGTH_LONG).show()
                }
            )
        }

        findViewById<MaterialTextView>(R.id.tvGoToRegister).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        findViewById<MaterialTextView>(R.id.tvRecoverPassword).setOnClickListener {
            startActivity(Intent(this, RecoverPasswordActivity::class.java))
        }
    }

    private fun login() {
        val email = findViewById<TextInputEditText>(R.id.editTextEmail)
            .text?.toString()?.trim().orEmpty()
        val password = findViewById<TextInputEditText>(R.id.editTextPassword)
            .text?.toString().orEmpty()

        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Completa todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val result = authRepository.login(email, password)
                val user = checkNotNull(result.user) {
                    "Firebase Auth devolvió un resultado sin usuario."
                }
                sessionManager.uid = user.uid
                sessionManager.email = user.email ?: email
                sessionManager.role = null
                sessionManager.fullName = null
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            } catch (e: FirebaseException) {
                Toast.makeText(
                    this@LoginActivity,
                    "No se pudo iniciar sesión: ${e.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
