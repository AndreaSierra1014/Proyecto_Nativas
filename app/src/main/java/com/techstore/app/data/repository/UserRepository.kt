package com.techstore.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.techstore.app.data.model.User
import com.techstore.app.util.Constants
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun listAll(): List<User> =
        db.collection(Constants.COLLECTION_USERS).get().await().toObjects(User::class.java)

    suspend fun update(user: User) =
        db.collection(Constants.COLLECTION_USERS).document(user.uid).set(user).await()

    suspend fun delete(uid: String) {
        db.collection(Constants.COLLECTION_USERS).document(uid).delete().await()
    }
}
