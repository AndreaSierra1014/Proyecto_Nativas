package com.techstore.app.ui.admin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.techstore.app.data.model.User
import com.techstore.app.data.repository.UserRepository
import com.techstore.app.databinding.ActivityUserCrudBinding
import kotlinx.coroutines.launch

class UserCrudActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserCrudBinding
    private val repository = UserRepository()
    private val adapter = UserAdapter(
        onEdit = ::toggleActive,
        onDelete = ::confirmDelete
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserCrudBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        binding.rvUsers.adapter = adapter
        loadUsers()
    }

    private fun loadUsers() {
        lifecycleScope.launch {
            try {
                adapter.submit(repository.listAll())
            } catch (error: Exception) {
                Toast.makeText(
                    this@UserCrudActivity,
                    "No se pudieron cargar los usuarios: ${error.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun toggleActive(user: User) {
        lifecycleScope.launch {
            try {
                repository.update(user.copy(activo = !user.activo))
                loadUsers()
            } catch (error: Exception) {
                Toast.makeText(
                    this@UserCrudActivity,
                    "No se pudo actualizar el usuario: ${error.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun confirmDelete(user: User) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar usuario")
            .setMessage("¿Eliminar ${user.email}? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                lifecycleScope.launch {
                    try {
                        repository.delete(user.uid)
                        loadUsers()
                    } catch (error: Exception) {
                        Toast.makeText(
                            this@UserCrudActivity,
                            "No se pudo eliminar el usuario: ${error.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
