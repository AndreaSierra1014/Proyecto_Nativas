package com.techstore.app.ui.buyer

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.techstore.app.data.repository.OrderRepository
import com.techstore.app.databinding.ActivityCartBinding
import com.techstore.app.util.toCOP
import kotlinx.coroutines.launch

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private val repository = OrderRepository()
    private val uid: String
        get() = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnCheckout.setOnClickListener {
            startActivity(Intent(this, CheckoutActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadCart()
    }

    private fun loadCart() {
        lifecycleScope.launch {
            try {
                val items = repository.getCart(uid)
                binding.tvCart.text = if (items.isEmpty()) {
                    "Tu carrito está vacío."
                } else {
                    items.joinToString("\n\n") {
                        "${it.nombre} ×${it.cantidad} — ${it.subtotal.toCOP()}"
                    }
                }
                binding.tvTotal.text =
                    "Total: ${items.sumOf { it.subtotal }.toCOP()}"
                binding.btnCheckout.isEnabled = items.isNotEmpty()
            } catch (error: Exception) {
                Toast.makeText(
                    this@CartActivity,
                    "No se pudo cargar el carrito: ${error.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
