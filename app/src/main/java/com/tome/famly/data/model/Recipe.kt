package com.tome.famly.data.model

import com.google.firebase.Timestamp

data class Recipe(
    val id: String,
    val title: String,
    val description: String? = null,
    val link: String? = null,
    val createdAt: Timestamp
)