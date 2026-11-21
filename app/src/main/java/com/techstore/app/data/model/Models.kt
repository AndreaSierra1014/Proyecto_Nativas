package com.techstore.app.data.model

import com.google.firebase.firestore.GeoPoint
import java.io.Serializable

data class UserModel(
    val uid: String = "",
    val email: String = "",
    val fullName: String = "",
    val phone: String = "",
    val role: String = "buyer",
    val address: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class ProductModel(
    val id: String = "",
    val name: String = "",
    val brand: String = "",
    val price: Double = 0.0,
    val oldPrice: Double = 0.0,
    val description: String = "",
    val imageUrl: String = "",
    val stock: Int = 0,
    val category: String = "Celulares",
    val rating: Float = 0f,
    val reviews: Int = 0,
    val tag: String = "",
    val sellerId: String = "",
    val status: String = "active"
)

data class CartItem(
    val productId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val quantity: Int = 1,
    val imageUrl: String = ""
) : Serializable

data class OrderModel(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val items: List<CartItem> = emptyList(),
    val total: Double = 0.0,
    val status: String = "pending",
    val paymentStatus: String = "unpaid",
    val deliveryAddress: String = "",
    val location: GeoPoint? = null,
    val paymentMethod: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
