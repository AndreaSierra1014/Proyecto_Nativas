package com.techstore.app.ui.auth

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.techstore.app.data.model.Role
import com.techstore.app.data.repository.AuthRepository
import com.techstore.app.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val repository = AuthRepository()
    private val publicRoles = listOf(Role.BUYER, Role.SELLER)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.spinnerRole.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            publicRoles.map { it.name }
        )
        binding.btnRegister.setOnClickListener { register() }
    }

    private fun register() {
        val name = binding.etNombre.text?.toString()?.trim().orEmpty()
        val email = binding.etEmail.text?.toString()?.trim().orEmpty()
        val password = binding.etPassword.text?.toString().orEmpty()
        if (name.isBlank() || email.isBlank() || password.length < 6) {
            Toast.makeText(
                this,
                "Completa los campos y usa una contraseña de al menos 6 caracteres.",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        val role = publicRoles[binding.spinnerRole.selectedItemPosition]

        binding.btnRegister.isEnabled = false
        lifecycleScope.launch {
            try {
                repository.register(name, email, password, role)
                Toast.makeText(this@RegisterActivity, "Cuenta creada.", Toast.LENGTH_SHORT).show()
                finish()
            } catch (error: Exception) {
                Toast.makeText(
                    this@RegisterActivity,
                    "No se pudo registrar la cuenta: ${error.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                binding.btnRegister.isEnabled = true
            }
        }
    }
}
