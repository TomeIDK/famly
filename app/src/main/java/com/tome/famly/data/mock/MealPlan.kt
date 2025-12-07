package com.tome.famly.data.mock

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.tome.famly.data.model.MealPlan
import com.tome.famly.ui.screens.getCurrentWeekDates
val week = getCurrentWeekDates()

val mockMealPlans = mutableStateListOf(
        MealPlan(week[0], mutableStateOf("Spaghetti Bolognese")),
        MealPlan(week[2], mutableStateOf("Chicken Curry")),
        MealPlan(week[6], mutableStateOf("Beef Tacos"))
)

