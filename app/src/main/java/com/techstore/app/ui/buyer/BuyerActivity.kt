package com.techstore.app.ui.buyer

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.techstore.app.data.model.CartItem
import com.techstore.app.data.repository.OrderRepository
import com.techstore.app.data.repository.ProductRepository
import com.techstore.app.databinding.ActivityBuyerBinding
import com.techstore.app.ui.auth.LoginActivity
import kotlinx.coroutines.launch

class BuyerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBuyerBinding
    private val productRepository = ProductRepository()
    private val orderRepository = OrderRepository()
    private val uid: String
        get() = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    private val adapter = ProductBuyerAdapter { product ->
        lifecycleScope.launch {
            try {
                orderRepository.addToCart(
                    uid,
                    CartItem(
                        productId = product.id,
                        nombre = product.nombre,
                        precio = product.precio,
                        imagenUrl = product.imagenUrl
                    )
                )
                Toast.makeText(this@BuyerActivity, "Añadido al carrito.", Toast.LENGTH_SHORT).show()
                updateCartBadge()
            } catch (error: Exception) {
                Toast.makeText(
                    this@BuyerActivity,
                    "No se pudo añadir al carrito: ${error.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (uid.isBlank()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        binding = ActivityBuyerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.rvCatalog.layoutManager = GridLayoutManager(this, 2)
        binding.rvCatalog.adapter = adapter
        binding.btnCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
        binding.btnOrders.setOnClickListener {
            startActivity(Intent(this, OrdersActivity::class.java))
        }
        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        loadProducts()
    }

    override fun onResume() {
        super.onResume()
        updateCartBadge()
    }

    private fun loadProducts() {
        lifecycleScope.launch {
            try {
                adapter.submit(productRepository.listActive())
            } catch (error: Exception) {
                Toast.makeText(
                    this@BuyerActivity,
                    "No se pudieron cargar los productos: ${error.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun updateCartBadge() {
        lifecycleScope.launch {
            try {
                val count = orderRepository.getCart(uid).sumOf { it.cantidad }
                binding.btnCart.text = "Carrito ($count)"
            } catch (error: Exception) {
                Toast.makeText(
                    this@BuyerActivity,
                    "No se pudo actualizar el carrito: ${error.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
