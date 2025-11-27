package com.tome.famly.data.mock

import com.tome.famly.data.model.ShoppingList
import com.tome.famly.data.model.ShoppingListItem

val mockShoppingLists = listOf(
    ShoppingList(
        id = 1,
        title = "Colruyt",
        items = listOf(
            ShoppingListItem(id = 1, name = "Cheese"),
            ShoppingListItem(id = 2, name = "Butter"),
            ShoppingListItem(id = 3, name = "Mayonnaise")
        )
    ),
    ShoppingList(
        id = 2,
        title = "Aldi",
        items = listOf(
            ShoppingListItem(id = 4, name = "Cookies", isChecked = true),
            ShoppingListItem(id = 5, name = "Mug", isChecked = true)
        )
    ),
    ShoppingList(
        id = 3,
        title = "Markt",
        items = listOf(
            ShoppingListItem(id = 1, name = "Milk"),
            ShoppingListItem(id = 2, name = "Rotisserie chicken", isChecked = true),
            ShoppingListItem(id = 3, name = "Apples")
        )
    )
)