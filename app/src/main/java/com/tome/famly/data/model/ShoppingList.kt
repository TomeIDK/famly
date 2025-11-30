package com.tome.famly.data.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class ShoppingList(
    val id: Int,
    val title: String,
    val items: MutableList<ShoppingListItem>
)

data class ShoppingListItem(
    val id: Int,
    val name: String,
    var isChecked: MutableState<Boolean> = mutableStateOf(false)
)