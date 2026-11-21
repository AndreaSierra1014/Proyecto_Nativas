package com.techstore.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.techstore.app.MainActivity
import com.techstore.app.data.repository.AuthRepository
import com.techstore.app.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val repository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener { login() }
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.tvForgot.setOnClickListener { resetPassword() }
        binding.btnBiometric.setOnClickListener {
            if (!BiometricHelper.canAuthenticate(this)) {
                Toast.makeText(this, "Biometría no disponible.", Toast.LENGTH_SHORT).show()
            } else if (FirebaseAuth.getInstance().currentUser == null) {
                Toast.makeText(
                    this,
                    "Primero inicia sesión con tu correo y contraseña.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                BiometricHelper.prompt(this) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun login() {
        val email = binding.etEmail.text?.toString()?.trim().orEmpty()
        val password = binding.etPassword.text?.toString().orEmpty()
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Completa los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnLogin.isEnabled = false
        lifecycleScope.launch {
            try {
                repository.login(email, password)
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            } catch (error: Exception) {
                Toast.makeText(
                    this@LoginActivity,
                    "No se pudo iniciar sesión: ${error.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                binding.btnLogin.isEnabled = true
            }
        }
    }

    private fun resetPassword() {
        val email = binding.etEmail.text?.toString()?.trim().orEmpty()
        if (email.isBlank()) {
            Toast.makeText(this, "Ingresa tu correo primero.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                repository.resetPassword(email)
                Toast.makeText(
                    this@LoginActivity,
                    "Si existe una cuenta con ese correo, recibirás un enlace.",
                    Toast.LENGTH_LONG
                ).show()
            } catch (error: Exception) {
                Toast.makeText(
                    this@LoginActivity,
                    "No se pudo enviar el correo: ${error.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
