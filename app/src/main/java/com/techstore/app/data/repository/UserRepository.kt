package com.techstore.app.data.repository

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.ktx.Firebase
import com.techstore.app.data.model.UserModel
import com.techstore.app.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val db = Firebase.firestore

    fun observeUsers(): Flow<List<UserModel>> =
        db.collection(Constants.COL_USERS)
            .snapshots()
            .map { snapshot -> snapshot.toObjects(UserModel::class.java) }

    suspend fun updateUser(user: UserModel) =
        db.collection(Constants.COL_USERS).document(user.uid).set(user).await()

    suspend fun updateRole(uid: String, role: String) =
        db.collection(Constants.COL_USERS).document(uid)
            .update("role", role).await()

    suspend fun deleteProfile(uid: String) =
        db.collection(Constants.COL_USERS).document(uid).delete().await()
}
