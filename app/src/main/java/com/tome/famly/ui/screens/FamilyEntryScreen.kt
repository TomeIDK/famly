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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.tome.famly.data.CurrentUser
import com.tome.famly.data.FirestoreDB
import com.tome.famly.data.model.Family
import com.tome.famly.ui.navigation.Routes
import com.tome.famly.ui.viewmodels.FamilyEntryViewModel

@Composable
fun FamilyEntryScreen(navController: NavController, viewModel: FamilyEntryViewModel = viewModel()) {
    CurrentUser.currentFamily = null
    val userId = CurrentUser.uid ?: return
    val userFamilies by viewModel.userFamilies.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val navigateFamily by viewModel.navigateToHome.collectAsState()

    var newFamilyName by remember { mutableStateOf("") }
    var joinCode by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        viewModel.fetchUserFamilies()
    }

    LaunchedEffect(navigateFamily) {
        navigateFamily?.let { family ->
            CurrentUser.currentFamily = family
            navController.navigate(Routes.Home.name) {
                popUpTo(Routes.FamilyEntryScreen.name) { inclusive = true }
            }
            viewModel.clearNavigation()
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
            onClick = { viewModel.createFamily(newFamilyName) },
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
            onClick = { viewModel.joinFamily(joinCode) },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Join")
        }
    }
}
