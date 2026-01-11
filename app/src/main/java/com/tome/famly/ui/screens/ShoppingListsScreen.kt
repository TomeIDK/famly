package com.tome.famly.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tome.famly.ui.components.TopBar
import com.tome.famly.ui.navigation.Routes
import com.tome.famly.ui.theme.BackgroundColor
import com.tome.famly.ui.theme.LightBlue
import com.tome.famly.ui.theme.MutedTextColor
import com.tome.famly.ui.viewmodels.ShoppingListViewModel
import kotlin.random.Random

fun randomColor(): Color {
    val base = 0.25f
    val range = 0.6f
    return Color(
        red = base + Random.nextFloat() * range,
        green = base + Random.nextFloat() * range,
        blue = base + Random.nextFloat() * range,
        alpha = 1f
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListsScreen(navController: NavController, viewModel: ShoppingListViewModel = viewModel()) {
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopBar(
                title = "Shopping Lists",
                titleIcon = Icons.Outlined.ShoppingCart,
                titleIconColor = LightBlue,
                onBackClick = { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showBottomSheet = true },
                containerColor = LightBlue,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        var newListName by remember { mutableStateOf("") }
        ShoppingLists(modifier = Modifier.padding(innerPadding), navController = navController)

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = rememberModalBottomSheetState()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().drawBehind {
                            val strokeWidth = 1.dp.toPx()
                            val y = size.height - strokeWidth / 2
                            drawLine(
                                color = Color.Gray,
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = strokeWidth
                            )
                        }.padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,

                    ) {
                        Text(text = "New Shopping List", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                        Icon(Icons.Outlined.Close, contentDescription = "Close", modifier = Modifier.clickable { showBottomSheet = false })
                    }

                    Column {
                        Text("List Name", style = MaterialTheme.typography.labelLarge)
                        OutlinedTextField(
                            value = newListName,
                            onValueChange = { newListName = it },
                            label = { Text("e.g., Weekly Groceries") },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White,
                                unfocusedTextColor = MutedTextColor,
                                focusedLabelColor = LightBlue,
                                focusedIndicatorColor = LightBlue,
                            )
                        )
                        Text("Give your shopping list a memorable name", style = MaterialTheme.typography.labelMedium, color = MutedTextColor, modifier = Modifier.padding(top = 6.dp))
                    }
                    Button(onClick = {
                        if (newListName.isNotBlank()) {
                            viewModel.addShoppingList(newListName)
                            newListName = ""
                            showBottomSheet = false

                        } },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LightBlue
                        )
                    ) {
                        Text("Create List", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun ShoppingLists(modifier: Modifier = Modifier, navController: NavController) {
    val viewModel: ShoppingListViewModel = viewModel()
    val lists by viewModel.lists

    LaunchedEffect(Unit) {
        viewModel.loadLists()
    }

    LazyColumn(modifier = modifier.fillMaxSize().background(BackgroundColor)) {
        items(lists) { list ->
            ShoppingListCard(
                id = list.id,
                name = list.title,
                uncheckedItems = list.items.count { !it.isChecked },
                navController = navController
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShoppingListCard(id: String, name: String, uncheckedItems: Int, navController: NavController, viewModel: ShoppingListViewModel = viewModel()) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete list?") },
            text = { Text("Are you sure you want to delete '$name'") },
            confirmButton = {
                Button(onClick = {
                    viewModel.deleteShoppingList(id)
                    showDeleteDialog = false
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding()
            .combinedClickable(
                onClick = { navController.navigate("${Routes.ShoppingListDetail.name}/$id") },
                onLongClick = { showDeleteDialog = true }
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RectangleShape
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Outlined.ShoppingCart,
                contentDescription = null,
                modifier = Modifier
                    .padding(8.dp),
                tint = randomColor(),
            )
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                fontSize = 16.sp,
                fontWeight = FontWeight.W500
            )
            Spacer(Modifier.weight(1f))
            if (uncheckedItems > 0) {
                Box(
                    modifier = Modifier
                        .background(MutedTextColor.copy(alpha = 0.6f), shape = CircleShape)
                        .padding(vertical = 2.dp, horizontal = 10.dp),
                ) {
                    Text(
                        text = uncheckedItems.toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .background(LightBlue, shape = CircleShape)
                        .padding(vertical = 2.dp, horizontal = 2.dp),
                ) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                    )
                }
            }

        }
    }
}
