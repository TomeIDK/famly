package com.tome.famly.data.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.Timestamp

data class ShoppingList(
    val id: String,
    val title: String,
    val items: List<ShoppingListItem>,
    val createdAt: Timestamp
)

data class ShoppingListItem(
    val id: String,
    val name: String,
    var isChecked: Boolean = false
)

data class ShoppingListOverview(
    val id: String,
    val title: String,
    val uncheckedItemsCount: Int
)