package com.techstore.app.data.model

import com.google.firebase.firestore.DocumentId

data class Product(
    @DocumentId val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val stock: Int = 0,
    val imagenUrl: String = "",
    val sellerId: String = "",
    val categoria: String = "",
    val activo: Boolean = true
)
