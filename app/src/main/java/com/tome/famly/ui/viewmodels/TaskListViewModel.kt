package com.tome.famly.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import com.tome.famly.data.CurrentUser
import com.tome.famly.data.model.ShoppingList
import com.tome.famly.data.model.ShoppingListItem
import com.tome.famly.data.model.TaskListItem
import com.tome.famly.data.model.TaskList
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.collections.emptyList

class TaskListViewModel: ViewModel() {
    private val _lists = mutableStateOf<List<TaskList>>(emptyList())
    val lists: State<List<TaskList>> = _lists
    val familyId = CurrentUser.currentFamily?.id

    fun loadLists() {
        viewModelScope.launch {
            _lists.value = getAllTaskLists()
        }
    }

    suspend fun getAllTaskLists(): List<TaskList> {
        if (familyId.isNullOrEmpty()) return emptyList()

        val snapshot = Firebase.firestore
            .collection("families")
            .document(familyId)
            .collection("taskLists")
            .get()
            .await()

        return snapshot.documents.map { listDoc ->
            val itemsSnapshot = Firebase.firestore
                .collection("families")
                .document(familyId)
                .collection("taskLists")
                .document(listDoc.id)
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

            TaskList(
                id = listDoc.id,
                title = listDoc.getString("title") ?: "",
                items = items,
                createdAt = listDoc.getTimestamp("createdAt") ?: Timestamp(0, 0)
            )
        }
    }

    fun getTaskListById(id: String): TaskList? {
        return _lists.value.firstOrNull { it.id == id }
    }

    suspend fun fetchTaskListById(id: String): TaskList? {
        getTaskListById(id)?.let { return it }

        if (familyId.isNullOrEmpty()) return null

        val listDoc = Firebase.firestore
            .collection("families")
            .document(familyId)
            .collection("taskLists")
            .document(id)
            .get()
            .await()

        val itemsSnapshot = Firebase.firestore
            .collection("families")
            .document(familyId)
            .collection("taskLists")
            .document(id)
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

        return TaskList(
            id = listDoc.id,
            title = listDoc.getString("title") ?: "",
            items = items,
            createdAt = listDoc.getTimestamp("createdAt") ?: Timestamp(0, 0)
        )
    }

    fun toggleTaskChecked(taskListId: String, taskId: String, isChecked: Boolean) {
        if (familyId.isNullOrEmpty()) return

        viewModelScope.launch {
            try {
                Firebase.firestore
                    .collection("families")
                    .document(familyId)
                    .collection("taskLists")
                    .document(taskListId)
                    .collection("tasks")
                    .document(taskId)
                    .update("isChecked", isChecked)
                    .await()

                val updatedLists = _lists.value.map { list ->
                    if (list.id == taskListId) {
                        list.copy(
                            items = list.items.map { item ->
                                if (item.id == taskId) item.copy(isChecked = isChecked) else item
                            }
                        )
                    } else list
                }

                _lists.value = updatedLists

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addTaskList(title: String) {
        if (familyId.isNullOrEmpty()) {
            return
        }

        viewModelScope.launch {
            try {
                val docRef = Firebase.firestore
                    .collection("families")
                    .document(familyId)
                    .collection("taskLists")
                    .document()

                val newList = mapOf(
                    "title" to title,
                    "createdAt" to Timestamp.now()
                )

                docRef.set(newList).await()

                val updatedLists = _lists.value.toMutableList().apply {
                    add(
                        TaskList(
                            id = docRef.id,
                            title = title,
                            items = emptyList(),
                            createdAt = Timestamp.now()
                        )
                    )
                }

                _lists.value = updatedLists

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addItemToTaskList(taskListId: String, name: String) {
        if (familyId.isNullOrEmpty()) {
            return
        }

        viewModelScope.launch {
            try {
                val docRef = Firebase.firestore
                    .collection("families")
                    .document(familyId)
                    .collection("taskLists")
                    .document(taskListId)
                    .collection("tasks")
                    .document()

                val newItem = mapOf(
                    "name" to name,
                    "isChecked" to false
                )

                docRef.set(newItem).await()

                val updatedLists = _lists.value.map { list ->
                    if (list.id == taskListId) {
                        list.copy(
                            items = list.items + TaskListItem(
                                id = docRef.id,
                                name = name,
                                isChecked = false
                            )
                        )
                    } else list
                }

                _lists.value = updatedLists

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteTaskList(taskListId: String) {
        if (familyId.isNullOrEmpty()) return

        viewModelScope.launch {
            try {
                Firebase.firestore
                    .collection("families")
                    .document(familyId)
                    .collection("taskLists")
                    .document(taskListId)
                    .delete()
                    .await()

                _lists.value = _lists.value.filter { it.id != taskListId }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteTask(taskListId: String, taskId: String) {
        if (familyId.isNullOrEmpty()) return

        viewModelScope.launch {
            try {
                Firebase.firestore
                    .collection("families")
                    .document(familyId)
                    .collection("taskLists")
                    .document(taskListId)
                    .collection("tasks")
                    .document(taskId)
                    .delete()
                    .await()

                val updatedLists = _lists.value.map { list ->
                    if (list.id == taskListId) {
                        list.copy(items = list.items.filter { it.id != taskId })
                    } else list
                }
                _lists.value = updatedLists

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}