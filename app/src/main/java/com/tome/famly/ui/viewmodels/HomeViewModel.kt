package com.tome.famly.ui.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import com.tome.famly.data.CurrentUser
import com.tome.famly.data.FirestoreDB
import com.tome.famly.data.model.ShoppingList
import com.tome.famly.data.model.ShoppingListItem
import com.tome.famly.data.model.TaskList
import com.tome.famly.data.model.TaskListItem
import com.tome.famly.data.model.User
import com.tome.famly.data.model.WideCardStats
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.*
import kotlin.collections.emptyList

class HomeViewModel: ViewModel() {
    private val _members = mutableStateListOf<User>()
    val members: List<User> = _members
    val familyId = CurrentUser.currentFamily?.id


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

    fun getCurrentWeekDates(): List<LocalDate> {
        val today = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date

        val monday = today.minus(DatePeriod(days = today.dayOfWeek.ordinal))

        return (0..6).map { i ->
            monday.plus(DatePeriod(days = i))
        }
    }

    suspend fun getItemsToBuy(): Int {
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
            val dateStr = mealDoc.getString("date") ?: continue

            val mealDate = LocalDate.parse(dateStr)

            if (mealDate in weekDates) {
                count++
            }
        }

        return count
    }

    suspend fun getAllShoppingLists(): List<ShoppingList> {
        if (familyId.isNullOrEmpty()) {
            return emptyList()
        }

        val snapshot = Firebase.firestore
            .collection("families")
            .document(familyId)
            .collection("shoppingLists")
            .get()
            .await()

        return snapshot.documents.map { doc ->
                ShoppingList(
                    id = doc.id,
                    title = doc.getString("title") ?: "",
                    items = emptyList(),
                    createdAt = doc.getTimestamp("createdAt") ?: Timestamp(0, 0)
                )
        }
    }

    suspend fun getItemsForLists(): List<ShoppingList> {
        if (familyId.isNullOrEmpty()) {
            return emptyList()
        }

        val lists = getAllShoppingLists()

        return lists.map { list ->
            val itemsSnapshot = Firebase.firestore
                .collection("families")
                .document(familyId)
                .collection("shoppingLists")
                .document(list.id)
                .collection("items")
                .get()
                .await()

            val items = itemsSnapshot.documents.map { doc ->
                ShoppingListItem(
                    id = doc.id,
                    name = doc.getString("name") ?: "",
                    isChecked = doc.getBoolean("isChecked") ?: false
                )
            }

            list.copy(items = items)
        }
    }

    suspend fun getShoppingListsStats(): WideCardStats {
        val shoppingLists = getItemsForLists()

        val activeListsCount = shoppingLists.count()

        val firstListWithUnchecked = shoppingLists.firstOrNull { list ->
            list.items.any { !it.isChecked }
        }


        val firstListItemsLeft = firstListWithUnchecked?.items?.count { !it.isChecked } ?: 0

        val allItems = shoppingLists.flatMap { it.items }
        val totalItems = allItems.size
        val unchecked = allItems.count { !it.isChecked }

        return WideCardStats(
            activeListsCount = activeListsCount,
            firstListItemsLeft = firstListItemsLeft,
            firstListName = firstListWithUnchecked?.title ?: "",
            totalItems = totalItems,
            uncheckedItems = unchecked,
        )
    }

    suspend fun getAllTaskLists(): List<TaskList> {
        if (familyId.isNullOrEmpty()) {
            return emptyList()
        }

        val snapshot = Firebase.firestore
            .collection("families")
            .document(familyId)
            .collection("taskLists")
            .get()
            .await()

        return snapshot.documents.map { doc ->
            TaskList(
                id = doc.id,
                title = doc.getString("title") ?: "",
                items = emptyList(),
                createdAt = doc.getTimestamp("createdAt") ?: Timestamp(0, 0)
            )
        }
    }

    suspend fun getTasksForLists(): List<TaskList> {
        if (familyId.isNullOrEmpty()) {
            return emptyList()
        }

        val lists = getAllTaskLists()

        return lists.map { list ->
            val itemsSnapshot = Firebase.firestore
                .collection("families")
                .document(familyId)
                .collection("taskLists")
                .document(list.id)
                .collection("tasks")
                .get()
                .await()

            val items = itemsSnapshot.documents.map { doc ->
                TaskListItem(
                    id = doc.id,
                    name = doc.getString("name") ?: "",
                    isChecked = doc.getBoolean("isChecked") ?: false
                )
            }

            list.copy(items = items)
        }
    }

    suspend fun getTaskListsStats(): WideCardStats {
        val taskLists = getTasksForLists()

        val activeListsCount = taskLists.count()

        val firstListWithUnchecked = taskLists.firstOrNull { list ->
            list.items.any { !it.isChecked }
        }


        val firstListItemsLeft = firstListWithUnchecked?.items?.count { !it.isChecked } ?: 0

        val allItems = taskLists.flatMap { it.items }
        val totalItems = allItems.size
        val unchecked = allItems.count { !it.isChecked }

        return WideCardStats(
            activeListsCount = activeListsCount,
            firstListItemsLeft = firstListItemsLeft,
            firstListName = firstListWithUnchecked?.title ?: "",
            totalItems = totalItems,
            uncheckedItems = unchecked,
        )
    }
}