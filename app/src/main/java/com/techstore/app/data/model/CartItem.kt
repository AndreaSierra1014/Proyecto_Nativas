package com.techstore.app.data.model

import com.google.firebase.firestore.Exclude

data class CartItem(
    val productId: String = "",
    val nombre: String = "",
    val precio: Double = 0.0,
    val cantidad: Int = 1,
    val imagenUrl: String = ""
) {
    @get:Exclude
    val subtotal: Double
        get() = precio * cantidad
}
