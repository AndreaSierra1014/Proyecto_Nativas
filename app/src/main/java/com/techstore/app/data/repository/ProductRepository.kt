package com.techstore.app.data.repository

import android.net.Uri
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.techstore.app.data.model.ProductModel
import com.techstore.app.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ProductRepository {

    private val db = Firebase.firestore
    private val storage = Firebase.storage

    fun observeActiveProducts(): Flow<List<ProductModel>> =
        db.collection(Constants.COL_PRODUCTS)
            .whereEqualTo("status", "active")
            .snapshots()
            .map { snapshot -> snapshot.toObjects(ProductModel::class.java) }

    fun observeBySeller(sellerId: String): Flow<List<ProductModel>> =
        db.collection(Constants.COL_PRODUCTS)
            .whereEqualTo("sellerId", sellerId)
            .snapshots()
            .map { snapshot -> snapshot.toObjects(ProductModel::class.java) }

    fun observeAll(): Flow<List<ProductModel>> =
        db.collection(Constants.COL_PRODUCTS)
            .snapshots()
            .map { snapshot -> snapshot.toObjects(ProductModel::class.java) }

    suspend fun getById(id: String): ProductModel? =
        db.collection(Constants.COL_PRODUCTS).document(id).get().await()
            .toObject(ProductModel::class.java)

    suspend fun save(product: ProductModel, imageUri: Uri?): String {
        val id = product.id.ifBlank {
            db.collection(Constants.COL_PRODUCTS).document().id
        }
        val imageUrl =
            if (imageUri != null) uploadImage(imageUri, id) else product.imageUrl

        db.collection(Constants.COL_PRODUCTS).document(id)
            .set(product.copy(id = id, imageUrl = imageUrl)).await()
        return id
    }

    suspend fun delete(product: ProductModel) {
        if (product.imageUrl.isNotBlank()) {
            storage.getReferenceFromUrl(product.imageUrl).delete().await()
        }
        db.collection(Constants.COL_PRODUCTS).document(product.id).delete().await()
    }

    private suspend fun uploadImage(uri: Uri, productId: String): String {
        val ref = storage.reference
            .child("products/$productId/${UUID.randomUUID()}.jpg")
        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }
}
