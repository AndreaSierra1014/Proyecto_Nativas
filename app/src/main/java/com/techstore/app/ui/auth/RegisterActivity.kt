package com.techstore.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.FirebaseException
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.techstore.app.R
import com.techstore.app.data.local.SessionManager
import com.techstore.app.data.repository.AuthRepository
import com.techstore.app.ui.main.MainActivity
import com.techstore.app.util.Constants
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var authRepository: AuthRepository
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        authRepository = AuthRepository()
        sessionManager = SessionManager(this)

        val etName = findViewById<TextInputEditText>(R.id.editTextFullName)
        val etEmail = findViewById<TextInputEditText>(R.id.editTextEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.editTextPassword)
        val etPhone = findViewById<TextInputEditText>(R.id.editTextPhone)
        val etAddress = findViewById<TextInputEditText>(R.id.editTextAddress)
        val btnRegister = findViewById<MaterialButton>(R.id.btnRegister)
        val tvBack = findViewById<MaterialTextView>(R.id.tvBackToLogin)

        btnRegister.setOnClickListener {
            val name = etName.text?.toString()?.trim().orEmpty()
            val email = etEmail.text?.toString()?.trim().orEmpty()
            val password = etPassword.text?.toString().orEmpty()
            val phone = etPhone.text?.toString()?.trim().orEmpty()
            val address = etAddress.text?.toString()?.trim().orEmpty()

            if (name.isBlank() || email.isBlank() || password.isBlank() ||
                phone.isBlank() || address.isBlank()
            ) {
                Toast.makeText(this, "Completa todos los campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.length < 6) {
                Toast.makeText(
                    this,
                    "La contraseña debe tener al menos 6 caracteres.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            btnRegister.isEnabled = false
            lifecycleScope.launch {
                try {
                    val result = authRepository.register(
                        email = email,
                        password = password,
                        fullName = name,
                        phone = phone,
                        address = address,
                        role = Constants.ROLE_BUYER
                    )
                    val user = checkNotNull(result.user) {
                        "Firebase Auth devolvió un resultado sin usuario."
                    }
                    sessionManager.uid = user.uid
                    sessionManager.email = user.email ?: email
                    sessionManager.role = Constants.ROLE_BUYER
                    sessionManager.fullName = name
                    Toast.makeText(this@RegisterActivity, "Registro exitoso.", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                    finish()
                } catch (e: FirebaseException) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "No se pudo completar el registro: ${e.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                } finally {
                    btnRegister.isEnabled = true
                }
            }
        }

        tvBack.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
