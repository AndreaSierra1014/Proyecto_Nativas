package com.techstore.app.data.repository

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.ktx.Firebase
import com.techstore.app.data.model.OrderModel
import com.techstore.app.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class OrderRepository {

    private val db = Firebase.firestore

    suspend fun create(order: OrderModel): String {
        val ref = db.collection(Constants.COL_ORDERS).document()
        ref.set(order.copy(id = ref.id)).await()
        return ref.id
    }

    fun observeByUser(userId: String): Flow<List<OrderModel>> =
        db.collection(Constants.COL_ORDERS)
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot -> snapshot.toObjects(OrderModel::class.java) }

    fun observeAll(): Flow<List<OrderModel>> =
        db.collection(Constants.COL_ORDERS)
            .snapshots()
            .map { snapshot -> snapshot.toObjects(OrderModel::class.java) }

    suspend fun updateStatus(orderId: String, status: String) =
        db.collection(Constants.COL_ORDERS).document(orderId)
            .update("status", status).await()

}
