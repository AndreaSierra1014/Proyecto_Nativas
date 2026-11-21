package com.techstore.app.data.model

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId val uid: String = "",
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val role: Role = Role.BUYER,
    val direccion: String = "",
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val activo: Boolean = true
)
