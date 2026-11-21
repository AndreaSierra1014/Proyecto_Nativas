package com.techstore.app.ui.buyer

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.techstore.app.data.model.Order
import com.techstore.app.data.repository.OrderRepository
import com.techstore.app.databinding.ActivityCheckoutBinding
import com.techstore.app.ui.common.MapPickerActivity
import kotlinx.coroutines.launch

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private val repository = OrderRepository()
    private val uid: String
        get() = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    private var latitude = 0.0
    private var longitude = 0.0

    private val mapPicker =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                latitude = result.data?.getDoubleExtra(MapPickerActivity.EXTRA_LATITUDE, 0.0) ?: 0.0
                longitude = result.data?.getDoubleExtra(MapPickerActivity.EXTRA_LONGITUDE, 0.0) ?: 0.0
                binding.tvLocation.text = "Lat: $latitude, Lon: $longitude"
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPickLocation.setOnClickListener {
            mapPicker.launch(Intent(this, MapPickerActivity::class.java))
        }
        binding.btnPay.text = getString(com.techstore.app.R.string.simulate_order)
        binding.btnPay.setOnClickListener { createDemoOrder() }
    }

    private fun createDemoOrder() {
        val address = binding.etAddress.text?.toString()?.trim().orEmpty()
        if (address.isBlank()) {
            Toast.makeText(this, "Ingresa la dirección de entrega.", Toast.LENGTH_SHORT).show()
            return
        }
        if (uid.isBlank()) {
            Toast.makeText(this, "Inicia sesión para continuar.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.btnPay.isEnabled = false
        lifecycleScope.launch {
            try {
                val cart = repository.getCart(uid)
                if (cart.isEmpty()) {
                    Toast.makeText(this@CheckoutActivity, "El carrito está vacío.", Toast.LENGTH_SHORT)
                        .show()
                    return@launch
                }

                val order = repository.createOrderAndClearCart(
                    Order(
                        buyerId = uid,
                        buyerEmail = FirebaseAuth.getInstance().currentUser?.email.orEmpty(),
                        items = cart,
                        total = cart.sumOf { it.subtotal },
                        estado = "PENDIENTE",
                        direccionEntrega = address,
                        latitud = latitude,
                        longitud = longitude,
                        metodoPago = "DEMO"
                    )
                )
                Toast.makeText(
                    this@CheckoutActivity,
                    "Orden ${order.id} creada en modo demo. No se realizó ningún cobro.",
                    Toast.LENGTH_LONG
                ).show()
                startActivity(Intent(this@CheckoutActivity, OrdersActivity::class.java))
                finish()
            } catch (error: Exception) {
                Toast.makeText(
                    this@CheckoutActivity,
                    "No se pudo crear la orden: ${error.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                binding.btnPay.isEnabled = true
            }
        }
    }
}
