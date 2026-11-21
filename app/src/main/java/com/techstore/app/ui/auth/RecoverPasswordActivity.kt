package com.techstore.app.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.FirebaseException
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.techstore.app.R
import com.techstore.app.data.repository.AuthRepository
import kotlinx.coroutines.launch

class RecoverPasswordActivity : AppCompatActivity() {

    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover_password)
        authRepository = AuthRepository()

        val etEmail = findViewById<TextInputEditText>(R.id.editTextEmail)
        val btnSend = findViewById<MaterialButton>(R.id.btnSend)
        val tvBack = findViewById<MaterialTextView>(R.id.tvBackToLogin)

        btnSend.setOnClickListener {
            val email = etEmail.text?.toString()?.trim().orEmpty()
            if (email.isBlank()) {
                Toast.makeText(this, "Completa el correo.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            btnSend.isEnabled = false
            lifecycleScope.launch {
                try {
                    authRepository.sendPasswordReset(email)
                    Toast.makeText(
                        this@RecoverPasswordActivity,
                        "Si existe una cuenta para ese correo, recibirás un enlace de recuperación.",
                        Toast.LENGTH_LONG
                    ).show()
                } catch (e: FirebaseException) {
                    Toast.makeText(
                        this@RecoverPasswordActivity,
                        "No se pudo enviar el enlace. Inténtalo de nuevo.",
                        Toast.LENGTH_LONG
                    ).show()
                } finally {
                    btnSend.isEnabled = true
                }
            }
        }

        tvBack.setOnClickListener { finish() }
    }
}
