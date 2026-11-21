package com.techstore.app.data.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.techstore.app.data.model.UserModel
import com.techstore.app.util.Constants
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = Firebase.auth,
    private val db: FirebaseFirestore = Firebase.firestore
) {

    suspend fun login(email: String, password: String): AuthResult =
        auth.signInWithEmailAndPassword(email.trim(), password).await()

    suspend fun register(
        email: String,
        password: String,
        fullName: String,
        phone: String,
        address: String,
        role: String
    ): AuthResult {
        require(role == Constants.ROLE_BUYER || role == Constants.ROLE_SELLER) {
            "El rol de registro no es válido."
        }

        val normalizedEmail = email.trim()
        val result =
            auth.createUserWithEmailAndPassword(normalizedEmail, password).await()
        val uid = checkNotNull(result.user) {
            "Firebase Auth devolvió un resultado sin usuario."
        }.uid

        val user = UserModel(
            uid = uid,
            email = normalizedEmail,
            fullName = fullName.trim(),
            phone = phone.trim(),
            role = role,
            address = address.trim()
        )
        db.collection(Constants.COL_USERS).document(uid).set(user).await()
        return result
    }

    suspend fun sendPasswordReset(email: String) {
        auth.sendPasswordResetEmail(email.trim()).await()
    }

    fun logout() = auth.signOut()

    fun currentUid(): String? = auth.currentUser?.uid
}
