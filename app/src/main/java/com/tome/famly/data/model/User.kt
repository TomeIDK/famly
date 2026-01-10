package com.tome.famly.data.model

data class User(
    val uid: String,
    val displayName: String = "",
    val email: String = "",
    val role: String = "Member"
)