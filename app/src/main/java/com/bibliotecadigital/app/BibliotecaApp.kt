package com.bibliotecadigital.app

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestoreSettings

class BibliotecaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Ativar persistência offline no Firestore
        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        FirebaseFirestore.getInstance().firestoreSettings = settings
    }
}