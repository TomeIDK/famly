package com.tome.famly.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.tome.famly.data.CurrentUser
import com.tome.famly.data.FirestoreDB
import com.tome.famly.data.model.Family
import com.tome.famly.ui.navigation.Routes

@Composable
fun FamilyEntryScreen(navController: NavController) {
    CurrentUser.currentFamily = null
    val userId = CurrentUser.uid ?: return
    var userFamilies by remember { mutableStateOf(listOf<Family>()) }
    var isLoading by remember { mutableStateOf(true) }
    var newFamilyName by remember { mutableStateOf("") }
    var joinCode by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        FirestoreDB.db.collectionGroup("families")
            .get()
            .addOnSuccessListener { snapshot ->
                val families = snapshot.mapNotNull { doc ->
                    val family = doc.toObject(Family::class.java).apply { id = doc.id }
                    if (family.members.contains(userId)) family else null
                }
                userFamilies = families
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp, start = 16.dp, end = 16.dp, bottom = 32.dp)
    ) {
        Text("Your Families", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(8.dp))

        // fetch all families for user and display list to select one
        if (isLoading) {
            Text("Fetching families...", style = MaterialTheme.typography.bodyMedium)
        } else if (userFamilies.isEmpty()) {
            Text("You are not in any families yet.", style = MaterialTheme.typography.bodyMedium)
            Text(userId)
        } else {
            LazyColumn {
                items(userFamilies.size) { index ->
                    val family = userFamilies[index]
                    Card(
                        modifier = Modifier.fillMaxWidth()
                            .padding(vertical = 8.dp)

                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                            ) {
                                Text(
                                    "Famly ${family.name}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    "${family.members.size} members",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Button(onClick = {
                                CurrentUser.currentFamily = family
                                navController.navigate(Routes.Home.name)
                            }) {
                                Text("Select")
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // create family
        Text("Create a New Family", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = newFamilyName,
            onValueChange = { newFamilyName = it },
            label = { Text("Family Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                if (newFamilyName.isNotBlank()) {
                    generateJoinCode { joinCode ->
                        val newFamily = Family(
                            id = generateFamilyId(),
                            name = newFamilyName,
                            members = listOf(userId),
                            joinCode = joinCode,
                            createdAt = Timestamp.now()
                        )
                        FirestoreDB.db.collection("families")
                            .document(newFamily.id)
                            .set(newFamily)
                    }
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Create")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // join family
        Text("Join a Family", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = joinCode,
            onValueChange = { joinCode = it },
            label = { Text("Family Code") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                if (joinCode.isNotBlank()) {
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
                                }
                            }
                        }
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Join")
        }
    }
}

fun generateFamilyId(): String {
    val ref = FirestoreDB.db.collection("families").document()
    return ref.id
}

fun generateJoinCode(length: Int = 6, callback: (String) -> Unit) {
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
