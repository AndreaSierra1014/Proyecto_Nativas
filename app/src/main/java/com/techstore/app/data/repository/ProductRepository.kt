package com.techstore.app.data.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.techstore.app.data.model.Product
import com.techstore.app.util.Constants
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ProductRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {

    suspend fun listAll(): List<Product> =
        db.collection(Constants.COLLECTION_PRODUCTS).get().await().toObjects(Product::class.java)

    suspend fun listActive(): List<Product> =
        db.collection(Constants.COLLECTION_PRODUCTS)
            .whereEqualTo("activo", true)
            .get().await().toObjects(Product::class.java)

    suspend fun listBySeller(sellerId: String): List<Product> =
        db.collection(Constants.COLLECTION_PRODUCTS)
            .whereEqualTo("sellerId", sellerId)
            .get().await().toObjects(Product::class.java)

    suspend fun create(product: Product, imageUri: Uri?): Product {
        val id = db.collection(Constants.COLLECTION_PRODUCTS).document().id
        val url = imageUri?.let { uploadImage(it, product.sellerId) } ?: product.imagenUrl
        val savedProduct = product.copy(id = id, imagenUrl = url)
        db.collection(Constants.COLLECTION_PRODUCTS).document(id).set(savedProduct).await()
        return savedProduct
    }

    suspend fun update(product: Product, imageUri: Uri?) {
        val url = imageUri?.let { uploadImage(it, product.sellerId) } ?: product.imagenUrl
        db.collection(Constants.COLLECTION_PRODUCTS).document(product.id)
            .set(product.copy(imagenUrl = url)).await()
    }

    suspend fun getById(id: String): Product? =
        db.collection(Constants.COLLECTION_PRODUCTS).document(id).get().await()
            .toObject(Product::class.java)

    suspend fun delete(id: String) {
        db.collection(Constants.COLLECTION_PRODUCTS).document(id).delete().await()
    }

    private suspend fun uploadImage(uri: Uri, sellerId: String): String {
        require(sellerId.isNotBlank()) { "El producto debe tener un vendedor asignado." }
        val ref = storage.reference.child("products/$sellerId/${UUID.randomUUID()}.jpg")
        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }
}
