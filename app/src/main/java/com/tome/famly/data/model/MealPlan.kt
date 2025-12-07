package com.tome.famly.data.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.datetime.LocalDate

data class MealPlan(
    val date: LocalDate,
    var recipe: MutableState<String?> = mutableStateOf(null)
)