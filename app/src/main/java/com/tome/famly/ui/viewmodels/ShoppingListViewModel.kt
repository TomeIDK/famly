package com.tome.famly.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import com.tome.famly.data.CurrentUser
import com.tome.famly.data.model.ShoppingList
import com.tome.famly.data.model.ShoppingListItem
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.collections.emptyList
import androidx.compose.runtime.State

class ShoppingListViewModel: ViewModel() {

    private val _lists = mutableStateOf<List<ShoppingList>>(emptyList())
    val lists: State<List<ShoppingList>> = _lists
    val familyId = CurrentUser.currentFamily?.id

    fun loadLists() {
        viewModelScope.launch {
            _lists.value = getAllShoppingLists()
        }
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

        return snapshot.documents.map { listDoc ->
            val itemsSnapshot = Firebase.firestore
                .collection("families")
                .document(familyId)
                .collection("shoppingLists")
                .document(listDoc.id)
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

            ShoppingList(
                id = listDoc.id,
                title = listDoc.getString("title") ?: "",
                items = items,
                createdAt = listDoc.getTimestamp("createdAt") ?: Timestamp(0, 0)
            )
        }
    }

    fun getShoppingListById(id: String): ShoppingList? {
        return _lists.value.firstOrNull { it.id == id }
    }

    suspend fun fetchShoppingListById(id: String): ShoppingList? {
        getShoppingListById(id)?.let { return it}

        if (familyId.isNullOrEmpty()) {
            return null
        }

        val listDoc = Firebase.firestore
            .collection("families")
            .document(familyId)
            .collection("shoppingLists")
            .document(id)
            .get()
            .await()

        val itemsSnapshot = Firebase.firestore
            .collection("families")
            .document(familyId)
            .collection("shoppingLists")
            .document(id)
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

        return ShoppingList(
            id = listDoc.id,
            title = listDoc.getString("title") ?: "",
            items = items,
            createdAt = listDoc.getTimestamp("createdAt") ?: Timestamp(0, 0)
        )
    }

    fun toggleItemChecked(shoppingListId: String, itemId: String, isChecked: Boolean) {
        if (familyId.isNullOrEmpty()) {
            return
        }

        viewModelScope.launch {
            try {
                Firebase.firestore
                    .collection("families")
                    .document(familyId)
                    .collection("shoppingLists")
                    .document(shoppingListId)
                    .collection("items")
                    .document(itemId)
                    .update("isChecked", isChecked)
                    .await()

                val updatedLists = _lists.value.map { list ->
                    if (list.id == shoppingListId) {
                        list.copy(
                            items = list.items.map { item ->
                                if (item.id == itemId) item.copy(isChecked = isChecked) else item
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

    fun addShoppingList(title: String) {
        if (familyId.isNullOrEmpty()) {
            return
        }

        viewModelScope.launch {
            try {
                val docRef = Firebase.firestore
                    .collection("families")
                    .document(familyId)
                    .collection("shoppingLists")
                    .document()

                val newList = mapOf(
                    "title" to title,
                    "createdAt" to Timestamp.now()
                )

                docRef.set(newList).await()

                val updatedLists = _lists.value.toMutableList().apply {
                    add(
                        ShoppingList(
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

    fun addItemToShoppingList(shoppingListId: String, name: String) {
        if (familyId.isNullOrEmpty()) {
            return
        }

        viewModelScope.launch {
            try {
                val docRef = Firebase.firestore
                    .collection("families")
                    .document(familyId)
                    .collection("shoppingLists")
                    .document(shoppingListId)
                    .collection("items")
                    .document()

                val newItem = mapOf(
                    "name" to name,
                    "isChecked" to false
                )

                docRef.set(newItem).await()

                val updatedLists = _lists.value.map { list ->
                    if (list.id == shoppingListId) {
                        list.copy(
                            items = list.items + ShoppingListItem(
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

    fun deleteShoppingList(shoppingListId: String) {
        if (familyId.isNullOrEmpty()) return

        viewModelScope.launch {
            try {
                Firebase.firestore
                    .collection("families")
                    .document(familyId)
                    .collection("shoppingLists")
                    .document(shoppingListId)
                    .delete()
                    .await()

                _lists.value = _lists.value.filter { it.id != shoppingListId }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteShoppingListItem(shoppingListId: String, itemId: String) {
        if (familyId.isNullOrEmpty()) return

        viewModelScope.launch {
            try {
                Firebase.firestore
                    .collection("families")
                    .document(familyId)
                    .collection("shoppingLists")
                    .document(shoppingListId)
                    .collection("items")
                    .document(itemId)
                    .delete()
                    .await()

                val updatedLists = _lists.value.map { list ->
                    if (list.id == shoppingListId) {
                        list.copy(items = list.items.filter { it.id != itemId })
                    } else list
                }
                _lists.value = updatedLists

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}