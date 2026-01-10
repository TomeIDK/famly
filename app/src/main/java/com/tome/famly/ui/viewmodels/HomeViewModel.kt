package com.tome.famly.ui.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.tome.famly.data.CurrentUser
import com.tome.famly.data.FirestoreDB
import com.tome.famly.data.model.User
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.*

class HomeViewModel: ViewModel() {
    private val _members = mutableStateListOf<User>()
    val members: List<User> = _members

    fun getFamilyMembers() {
        val family = CurrentUser.currentFamily ?: return
        _members.clear()

        family.members.forEach { uid ->
            FirestoreDB.db.collection("users").document(uid)
                .get()
                .addOnSuccessListener { doc ->
                    val displayName = doc.getString("displayName") ?: ""
                    val email = doc.getString("email") ?: ""
                    val role = if (uid == family.createdBy) "Owner" else "Member"
                    _members.add(User(uid, displayName, email, role))
                }
        }
    }

    suspend fun getItemsToBuy(): Int {
        val familyId = CurrentUser.currentFamily?.id

        if (familyId.isNullOrEmpty()) {
            return 0
        }

        var count = 0

        val shoppingLists = Firebase.firestore
            .collection("families")
            .document(familyId)
            .collection("shoppingLists")
            .get()
            .await()

        for (listDoc in shoppingLists.documents) {
            val items = Firebase.firestore
                .collection("families")
                .document(familyId)
                .collection("shoppingLists")
                .document(listDoc.id)
                .collection("items")
                .whereEqualTo("isChecked", false)
                .get()
                .await()

            count += items.size()
        }
            return count
    }

    suspend fun getTasksDue(): Int {
        val familyId = CurrentUser.currentFamily?.id

        if (familyId.isNullOrEmpty()) {
            return 0
        }
        var count = 0

        val taskLists = Firebase.firestore
            .collection("families")
            .document(familyId)
            .collection("taskLists")
            .get()
            .await()

        for (listDoc in taskLists.documents) {
            val tasks = Firebase.firestore
                .collection("families")
                .document(familyId)
                .collection("taskLists")
                .document(listDoc.id)
                .collection("tasks")
                .whereEqualTo("isChecked", false)
                .get()
                .await()

            count += tasks.size()
        }
        return count
    }

    suspend fun getMealsPlannedCount(): Int {
        val familyId = CurrentUser.currentFamily?.id
        val weekDates = getCurrentWeekDates()
        var count = 0

        if (familyId.isNullOrEmpty()) {
            return 0
        }

        val mealPlans = Firebase.firestore
            .collection("families")
            .document(familyId)
            .collection("mealPlans")
            .get()
            .await()

        for (mealDoc in mealPlans.documents) {
            val timestamp = mealDoc.getTimestamp("date") ?: continue

            val mealDate = timestamp.toDate().toInstant().toKotlinInstant().toLocalDateTime(TimeZone.currentSystemDefault()).date

            if (mealDate in weekDates) {
                count++
            }
        }

        return count
    }


    fun getCurrentWeekDates(): List<LocalDate> {
        val today = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date

        val monday = today.minus(DatePeriod(days = today.dayOfWeek.ordinal))

        return (0..6).map { i ->
            monday.plus(DatePeriod(days = i))
        }
    }
}