package com.techstore.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.techstore.app.data.model.Role
import com.techstore.app.data.model.User
import com.techstore.app.util.Constants
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun register(nombre: String, email: String, pass: String, role: Role): User {
        require(role != Role.ADMIN) {
            "No se puede registrar una cuenta administradora desde la app."
        }
        val normalizedEmail = email.trim()
        val result = auth.createUserWithEmailAndPassword(normalizedEmail, pass).await()
        val uid = checkNotNull(result.user?.uid) {
            "Firebase no devolvió el UID de la cuenta."
        }
        val user = User(
            uid = uid,
            nombre = nombre.trim(),
            email = normalizedEmail,
            role = role
        )
        try {
            db.collection(Constants.COLLECTION_USERS).document(uid).set(user).await()
        } catch (error: Exception) {
            try {
                result.user?.delete()?.await()
            } catch (cleanupError: Exception) {
                error.addSuppressed(cleanupError)
            }
            auth.signOut()
            throw error
        }
        return user
    }

    suspend fun login(email: String, pass: String): User {
        val result = auth.signInWithEmailAndPassword(email.trim(), pass).await()
        val uid = checkNotNull(result.user?.uid) {
            "Firebase no devolvió el UID de la cuenta."
        }
        return db.collection(Constants.COLLECTION_USERS).document(uid).get().await()
            .toObject(User::class.java)
            ?: error("No se encontró el perfil de esta cuenta.")
    }

    fun logout() = auth.signOut()

    suspend fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email.trim()).await()
    }

    fun currentUid(): String? = auth.currentUser?.uid
}
