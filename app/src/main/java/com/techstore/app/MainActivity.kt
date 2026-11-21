package com.techstore.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.techstore.app.data.model.Role
import com.techstore.app.data.model.User
import com.techstore.app.ui.admin.AdminActivity
import com.techstore.app.ui.auth.LoginActivity
import com.techstore.app.ui.buyer.BuyerActivity
import com.techstore.app.ui.seller.SellerActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            goToLogin()
            return
        }

        lifecycleScope.launch {
            try {
                val user = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .get()
                    .await()
                    .toObject(User::class.java)

                if (user == null || !user.activo) {
                    FirebaseAuth.getInstance().signOut()
                    goToLogin()
                } else {
                    route(user.role)
                }
            } catch (error: Exception) {
                FirebaseAuth.getInstance().signOut()
                Toast.makeText(
                    this@MainActivity,
                    "No se pudo cargar el perfil: ${error.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
                goToLogin()
            }
        }
    }

    private fun route(role: Role) {
        val activity = when (role) {
            Role.ADMIN -> AdminActivity::class.java
            Role.SELLER -> SellerActivity::class.java
            Role.BUYER -> BuyerActivity::class.java
        }
        startActivity(Intent(this, activity))
        finish()
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
