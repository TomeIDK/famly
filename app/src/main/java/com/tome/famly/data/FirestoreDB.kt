package com.tome.famly.data

import com.google.firebase.firestore.FirebaseFirestore

object FirestoreDB {
    val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
}