package com.tome.famly.data.mock

import com.tome.famly.data.model.MealPlan
import com.tome.famly.ui.screens.getCurrentWeekDates
val week = getCurrentWeekDates()

val mockMealPlans: List<MealPlan> = listOf(
        MealPlan(week[0], "Spaghetti Bolognese"),
        MealPlan(week[2], "Chicken Curry"),
        MealPlan(week[6], "Beef Tacos")
    )

