package com.techstore.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.techstore.app.data.model.CartItem
import com.techstore.app.data.model.Order
import com.techstore.app.util.Constants
import kotlinx.coroutines.tasks.await

class OrderRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun addToCart(uid: String, item: CartItem) {
        val ref = db.collection(Constants.COLLECTION_USERS).document(uid)
            .collection("cart").document(item.productId)
        db.runTransaction { transaction ->
            val current = transaction.get(ref).toObject(CartItem::class.java)
            val updated = if (current == null) item else current.copy(
                cantidad = current.cantidad + item.cantidad
            )
            transaction.set(ref, updated)
        }.await()
    }

    suspend fun getCart(uid: String): List<CartItem> =
        db.collection(Constants.COLLECTION_USERS).document(uid).collection("cart")
            .get().await().toObjects(CartItem::class.java)

    suspend fun removeFromCart(uid: String, productId: String) {
        db.collection(Constants.COLLECTION_USERS).document(uid).collection("cart")
            .document(productId).delete().await()
    }

    suspend fun createOrderAndClearCart(order: Order): Order {
        val cart = db.collection(Constants.COLLECTION_USERS).document(order.buyerId)
            .collection("cart").get().await()
        if (cart.isEmpty) error("El carrito está vacío.")

        val orderRef = db.collection(Constants.COLLECTION_ORDERS).document()
        val savedOrder = order.copy(id = orderRef.id)
        val batch = db.batch().set(orderRef, savedOrder)
        cart.documents.forEach { batch.delete(it.reference) }
        batch.commit().await()
        return savedOrder
    }

    suspend fun listByBuyer(uid: String): List<Order> =
        db.collection(Constants.COLLECTION_ORDERS).whereEqualTo("buyerId", uid)
            .get().await().toObjects(Order::class.java)
}
