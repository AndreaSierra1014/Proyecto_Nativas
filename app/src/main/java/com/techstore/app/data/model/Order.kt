package com.techstore.app.data.model

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class Order(
    @DocumentId val id: String = "",
    val buyerId: String = "",
    val buyerEmail: String = "",
    val items: List<CartItem> = emptyList(),
    val total: Double = 0.0,
    val estado: String = "PENDIENTE",
    val direccionEntrega: String = "",
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val metodoPago: String = "DEMO",
    val transaccionId: String = "",
    val fecha: Date = Date()
)
