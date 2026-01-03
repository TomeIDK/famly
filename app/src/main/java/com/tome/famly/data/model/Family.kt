package com.tome.famly.data.model

data class Family(
    val id: String = "",
    val name: String = "",
    val members: List<String> = listOf()
)