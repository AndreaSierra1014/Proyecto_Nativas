package com.techstore.app.ui.buyer

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.techstore.app.data.repository.OrderRepository
import com.techstore.app.databinding.ActivityOrdersBinding
import com.techstore.app.util.toCOP
import kotlinx.coroutines.launch

class OrdersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            finish()
            return
        }

        lifecycleScope.launch {
            try {
                val orders = OrderRepository().listByBuyer(uid)
                binding.tvOrders.text = if (orders.isEmpty()) {
                    "Aún no tienes órdenes."
                } else {
                    orders.joinToString("\n\n") {
                        "Orden ${it.id}\nEstado: ${it.estado}\nTotal: ${it.total.toCOP()}\n${it.direccionEntrega}"
                    }
                }
            } catch (error: Exception) {
                Toast.makeText(
                    this@OrdersActivity,
                    "No se pudieron cargar las órdenes: ${error.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
