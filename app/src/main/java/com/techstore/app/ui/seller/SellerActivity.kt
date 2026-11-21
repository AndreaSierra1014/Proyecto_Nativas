package com.techstore.app.ui.seller

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.techstore.app.data.repository.AuthRepository
import com.techstore.app.data.repository.ProductRepository
import com.techstore.app.databinding.ActivitySellerBinding
import com.techstore.app.ui.auth.LoginActivity
import kotlinx.coroutines.launch

class SellerActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySellerBinding
    private val repository = ProductRepository()
    private val adapter = ProductAdapter(
        onEdit = { product ->
            startActivity(
                Intent(this, ProductFormActivity::class.java)
                    .putExtra(ProductFormActivity.EXTRA_PRODUCT_ID, product.id)
            )
        },
        onDelete = { product ->
            AlertDialog.Builder(this)
                .setTitle("Eliminar producto")
                .setMessage("¿Eliminar ${product.nombre}?")
                .setPositiveButton("Eliminar") { _, _ -> deleteProduct(product.id) }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySellerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvProducts.layoutManager = LinearLayoutManager(this)
        binding.rvProducts.adapter = adapter
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, ProductFormActivity::class.java))
        }
        binding.btnLogout.setOnClickListener {
            AuthRepository().logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadProducts()
    }

    private fun loadProducts() {
        val sellerId = FirebaseAuth.getInstance().currentUser?.uid
        if (sellerId == null) {
            Toast.makeText(this, "La sesión del vendedor expiró.", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        lifecycleScope.launch {
            try {
                adapter.submit(repository.listBySeller(sellerId))
            } catch (error: Exception) {
                Toast.makeText(
                    this@SellerActivity,
                    "No se pudieron cargar los productos: ${error.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun deleteProduct(id: String) {
        lifecycleScope.launch {
            try {
                repository.delete(id)
                loadProducts()
            } catch (error: Exception) {
                Toast.makeText(
                    this@SellerActivity,
                    "No se pudo eliminar el producto: ${error.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
