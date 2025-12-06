package com.tome.famly.data.model

import kotlinx.datetime.LocalDate

data class MealPlan(
    val date: LocalDate,
    val recipe: String?
)