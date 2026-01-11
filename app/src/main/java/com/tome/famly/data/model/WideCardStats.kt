package com.tome.famly.data.model

data class WideCardStats(
    val firstListName: String,
    val firstListItemsLeft: Int,
    val activeListsCount: Int,
    val totalItems: Int,
    val uncheckedItems: Int
)