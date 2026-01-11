package com.tome.famly.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.tome.famly.data.CurrentUser
import com.tome.famly.data.model.Recipe
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import com.tome.famly.data.model.MealPlan
import com.tome.famly.ui.screens.getCurrentWeekDates
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MealPlannerViewModel: ViewModel() {
    private val _recipes = mutableStateOf<List<Recipe>>(emptyList())
    val recipes: State<List<Recipe>> = _recipes

    private val _weekMealPlans = mutableStateOf<Map<String, MealPlan?>>(emptyMap())
    val weekMealPlans: State<Map<String, MealPlan?>> = _weekMealPlans

    val familyId = CurrentUser.currentFamily?.id

    fun getRecipes() {
        if (familyId.isNullOrEmpty()) {
            return
        }

        viewModelScope.launch {
            try {
                val snapshot = Firebase.firestore
                    .collection("families")
                    .document(familyId)
                    .collection("recipes")
                    .get()
                    .await()

                val recipeList = snapshot.documents.map { doc ->
                    Recipe(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        description = doc.getString("description"),
                        link = doc.getString("link"),
                        createdAt = doc.getTimestamp("createdAt") ?: Timestamp.now()
                    )
                }

                _recipes.value = recipeList

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getWeekMealPlans(dates: List<String>) {
        if (familyId.isNullOrEmpty()) {
            return
        }

        viewModelScope.launch {
            val map = mutableMapOf<String, MealPlan?>()
            val mealPlansRef = Firebase.firestore
                .collection("families")
                .document(familyId)
                .collection("mealPlans")

            val recipesRef = Firebase.firestore
                .collection("families")
                .document(familyId)
                .collection("recipes")

            dates.forEach { date ->
                val snapshot = mealPlansRef
                    .whereEqualTo("date", date)
                    .get()
                    .await()

                val mealPlanDoc = snapshot.documents.firstOrNull()
                val mealPlanRecipeId = mealPlanDoc?.getString("recipe")

                val recipeName = mealPlanRecipeId?.let { recipeId ->
                    recipesRef.document(recipeId).get().await().getString("title")
                }

                map[date] = MealPlan(date = date, recipe = recipeName)
            }

            _weekMealPlans.value = map
        }
    }

    fun setMealPlan(date: String, recipeId: String?) {
        if (familyId.isNullOrEmpty()) {
            return
        }

        viewModelScope.launch {
            val collectionRef = Firebase.firestore
                .collection("families")
                .document(familyId)
                .collection("mealPlans")

            val snapshot = collectionRef
                .whereEqualTo("date", date)
                .get()
                .await()

            val doc = snapshot.documents.firstOrNull()

            if (recipeId == null) {
                doc?.reference?.delete()?.await()
            } else {
                val data = mapOf(
                    "date" to date,
                    "recipe" to recipeId
                )
                if (doc != null) {
                    doc.reference.set(data).await()
                } else {
                    collectionRef.add(data).await()
                }
            }

            getWeekMealPlans(getCurrentWeekDates())
        }
    }

    fun addRecipe(title: String, description: String? = null, link: String? = null) {
        if (familyId.isNullOrEmpty()) {
            return
        }

        viewModelScope.launch {
            try {
                val docRef = Firebase.firestore
                    .collection("families")
                    .document(familyId)
                    .collection("recipes")
                    .document() // Auto-generated ID

                val newRecipeMap = mapOf(
                    "title" to title,
                    "description" to description,
                    "link" to link,
                    "createdAt" to Timestamp.now()
                )

                docRef.set(newRecipeMap).await()

                _recipes.value += Recipe(
                                    id = docRef.id,
                                    title = title,
                                    description = description,
                                    link = link,
                                    createdAt = Timestamp.now()
                                )

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteRecipe(recipeId: String) {
        if (familyId.isNullOrEmpty()) {
            return
        }

        viewModelScope.launch {
            try {
                Firebase.firestore
                    .collection("families")
                    .document(familyId)
                    .collection("recipes")
                    .document(recipeId)
                    .delete()
                    .await()

                _recipes.value = _recipes.value.filter { it.id != recipeId }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}