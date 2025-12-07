package com.tome.famly.data.mock

import com.tome.famly.data.model.Recipe

val mockRecipes = mutableListOf(
    Recipe(
        title = "Spaghetti Carbonara",
        description = "Classic Italian pasta.",
        link = "https://example.com/carbonara"
    ),
    Recipe(title = "Avocado Toast"),
    Recipe(title = "Banana Bread", link = "https://example.com/banana-bread")
)