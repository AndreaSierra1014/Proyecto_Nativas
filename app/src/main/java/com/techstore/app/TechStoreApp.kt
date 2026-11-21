package com.techstore.app

import android.app.Application
import com.google.firebase.FirebaseApp

class TechStoreApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
