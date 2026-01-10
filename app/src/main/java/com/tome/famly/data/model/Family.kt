package com.tome.famly.data.model

import com.google.firebase.Timestamp

data class Family(
    var id: String = "",
    val name: String = "",
    val joinCode: String = "",
    val members: List<String> = listOf(),
    val createdAt: Timestamp = Timestamp.now(),
    val createdBy: String = ""
)