package com.tome.famly.data.mock

import com.tome.famly.data.model.Family

// Mock list of families
val mockFamilies = mutableListOf(
    Family(id = "fam1", name = "Pas Famly", members = listOf("g9KR8GLxKaMo1rnkGKoOXEkdnAB3")),
    Family(id = "fam2", name = "Dubois Famly", members = listOf())
)

// Simple ID generator
fun generateFamilyId(): String {
    val chars = ('A'..'Z') + ('0'..'9')
    return (1..6).map { chars.random() }.joinToString("")
}

// Lookup by ID (used for join)
fun getFamilyById(id: String): Family? {
    return mockFamilies.find { it.id == id }
}
