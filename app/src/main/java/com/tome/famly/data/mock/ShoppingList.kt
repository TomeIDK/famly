package com.tome.famly.data.mock

import androidx.compose.runtime.mutableStateOf
import com.tome.famly.data.model.ShoppingList
import com.tome.famly.data.model.ShoppingListItem

val mockShoppingLists = listOf(
    ShoppingList(
        id = 1,
        title = "Colruyt",
        items = mutableListOf(
            ShoppingListItem(id = 1, name = "Cheese"),
            ShoppingListItem(id = 2, name = "Butter"),
            ShoppingListItem(id = 3, name = "Mayonnaise")
        )
    ),
    ShoppingList(
        id = 2,
        title = "Aldi",
        items = mutableListOf(
            ShoppingListItem(id = 4, name = "Cookies", isChecked = mutableStateOf(true)),
            ShoppingListItem(id = 5, name = "Mug", isChecked = mutableStateOf(true))
        )
    ),
    ShoppingList(
        id = 3,
        title = "Markt",
        items = mutableListOf(
            ShoppingListItem(id = 1, name = "Milk"),
            ShoppingListItem(id = 2, name = "Rotisserie chicken", isChecked = mutableStateOf(true)),
            ShoppingListItem(id = 3, name = "Apples")
        )
    )
)