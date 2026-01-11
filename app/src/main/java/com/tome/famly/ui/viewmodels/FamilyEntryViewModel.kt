package com.tome.famly.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.tome.famly.data.CurrentUser
import com.tome.famly.data.FirestoreDB
import com.tome.famly.data.model.Family
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.collections.emptyList

class FamilyEntryViewModel: ViewModel() {
    private val userId = CurrentUser.uid ?: ""

    private val _userFamilies = MutableStateFlow<List<Family>>(emptyList())
    val userFamilies: StateFlow<List<Family>> = _userFamilies

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _navigateToHome = MutableStateFlow<Family?>(null)
    val navigateToHome: StateFlow<Family?> = _navigateToHome

    fun fetchUserFamilies() {
        _isLoading.value = true
        FirestoreDB.db.collectionGroup("families")
            .get()
            .addOnSuccessListener { snapshot ->
                val families = snapshot.mapNotNull { doc ->
                    val family = doc.toObject(Family::class.java).apply { id = doc.id }
                    if (family.members.contains(userId)) family else null
                }
                _userFamilies.value = families
                _isLoading.value = false
            }
            .addOnFailureListener {
                _isLoading.value = false
            }
    }

    fun createFamily(familyName: String) {
        if (familyName.isBlank()) return

        generateJoinCode { joinCode ->
            val docRef = FirestoreDB.db.collection("families").document()

            val newFamily = Family(
                id = docRef.id,
                name = familyName,
                members = listOf(userId),
                joinCode = joinCode,
                createdAt = Timestamp.now(),
                createdBy = userId
            )

            docRef.set(newFamily)
                .addOnSuccessListener {
                    _navigateToHome.value = newFamily
                }
        }
    }

    fun joinFamily(joinCode: String) {
        if (joinCode.isBlank()) return

        FirestoreDB.db.collection("families")
            .whereEqualTo("joinCode", joinCode)
            .get()
            .addOnSuccessListener { snapshot ->
                val doc = snapshot.documents.firstOrNull()
                doc?.let { documentSnapshot ->
                    val family = documentSnapshot.toObject(Family::class.java)
                    if (family != null) {
                        val familyRef = documentSnapshot.reference
                        familyRef.update("members", FieldValue.arrayUnion(userId))
                            .addOnSuccessListener {
                                _navigateToHome.value = family
                            }
                    }
                }
            }
    }

    private fun generateFamilyId(): String {
        return FirestoreDB.db.collection("families").document().id
    }

    private fun generateJoinCode(length: Int = 6, callback: (String) -> Unit) {
        val chars = ('A'..'Z') + ('0'..'9')
        val code = (1..length).map { chars.random() }.joinToString("")

        FirestoreDB.db.collection("families")
            .whereEqualTo("joinCode", code)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    callback(code)
                } else {
                    generateJoinCode(length, callback)
                }
            }
    }

    fun clearNavigation() {
        _navigateToHome.value = null
    }
}