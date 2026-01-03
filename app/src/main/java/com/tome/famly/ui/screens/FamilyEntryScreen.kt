package com.tome.famly.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tome.famly.data.mock.generateFamilyId
import com.tome.famly.data.model.Family
import com.tome.famly.data.mock.getFamilyById

@Composable
fun FamilyEntryScreen(
    userId: String,
    existingFamilies: List<Family>,
    onFamilySelected: (Family) -> Unit,
    onFamilyCreated: (Family) -> Unit,
    onFamilyJoined: (Family) -> Unit
) {
    var familyName by remember { mutableStateOf("") }
    var joinCode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Your Families", style = MaterialTheme.typography.titleLarge)

        existingFamilies.forEach { family ->
            Button(onClick = { onFamilySelected(family) }, modifier = Modifier.padding(vertical = 4.dp)) {
                Text(family.name)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("Create a New Family", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = familyName,
            onValueChange = { familyName = it },
            label = { Text("Family Name") }
        )
        Button(
            onClick = {
                if(familyName.isNotBlank()) {
                    val family = Family(id = generateFamilyId(), name = familyName, members = listOf(userId))
                    onFamilyCreated(family)
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) { Text("Create") }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Join a Family", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = joinCode,
            onValueChange = { joinCode = it },
            label = { Text("Family Code") }
        )
        Button(
            onClick = {
                if(joinCode.isNotBlank()) {
                    val family = getFamilyById(joinCode)
                    family?.let { onFamilyJoined(it) }
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) { Text("Join") }
    }
}
