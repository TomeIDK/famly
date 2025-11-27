package com.tome.famly.data.model

data class ShoppingList(
    val id: Int,
    val title: String,
    val items: List<ShoppingListItem>
)

data class ShoppingListItem(
    val id: Int,
    val name: String,
    var isChecked: Boolean = false
)