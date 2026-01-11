package com.tome.famly.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tome.famly.ui.components.TopBar
import com.tome.famly.ui.theme.BackgroundColor
import com.tome.famly.ui.theme.LightBlue
import com.tome.famly.ui.theme.MutedTextColor
import com.tome.famly.ui.viewmodels.ShoppingListViewModel

@Composable
fun ShoppingListScreen(shoppingListId: String, navController: NavController, viewModel: ShoppingListViewModel = viewModel()) {
    val list = viewModel.getShoppingListById(shoppingListId)

    val listState = remember { mutableStateOf(list) }

    LaunchedEffect(shoppingListId) {
        if (listState.value == null) {
            listState.value = viewModel.fetchShoppingListById(shoppingListId)
        }
        viewModel.loadLists()
    }

    val shoppingList = listState.value

    if (shoppingList == null) {
        Text("List not found")
        return
    }

    Scaffold(
        topBar = {
            TopBar(
                title = shoppingList.title,
                titleIcon = Icons.Outlined.ShoppingCart,
                titleIconColor = LightBlue,
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        ShoppingList( modifier = Modifier.padding(innerPadding), shoppingListId = shoppingList.id)
    }
}

@Composable
fun ShoppingList(modifier: Modifier = Modifier, shoppingListId: String, viewModel: ShoppingListViewModel = viewModel()) {
    val lists by viewModel.lists
    val shoppingList = lists.firstOrNull { it.id == shoppingListId }

    if (shoppingList == null) {
        Text("List not found")
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        AddItemField(shoppingListId = shoppingList.id)
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
            ) {
                item {
                    ItemsChecked(shoppingList.items.count { it.isChecked }, shoppingList.items.count())
                }
                items(shoppingList.items) { item ->
                    ListItem(
                        name = item.name,
                        isChecked = item.isChecked,
                        onCheckedChange = { newChecked -> viewModel.toggleItemChecked(shoppingList.id, item.id, newChecked) },
                        onDelete = { viewModel.deleteShoppingListItem(shoppingList.id, item.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun AddItemField(shoppingListId: String, viewModel: ShoppingListViewModel = viewModel()) {
    var text by remember { mutableStateOf("") }

    TextField(
        value = text,
        onValueChange = { text = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color(0xFFF2F2F2),
        ),
        placeholder = { Text("Add an item...") },
        trailingIcon = {
            IconButton(onClick = {
                if (text.isNotBlank()) {
                    viewModel.addItemToShoppingList(shoppingListId, text)
                    text = ""
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = null, tint = LightBlue)
            }
        },
        singleLine = true
    )
}

@Composable
fun ItemsChecked(itemsChecked: Int, maxItems: Int) {
    Text("$itemsChecked of $maxItems items checked",
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp, top = 6.dp, end = 16.dp),
        textAlign = TextAlign.End,
        style = MaterialTheme.typography.labelLarge,
        color = MutedTextColor
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListItem(name: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit, onDelete: (() -> Unit)? = null) {
    var checked by remember { mutableStateOf(isChecked) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog && onDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete item?") },
            text = { Text("Are you sure you want to delete '$name'") },
            confirmButton = {
                Button(onClick = {
                    onDelete()
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
            .combinedClickable(onClick = {}, onLongClick = { showDeleteDialog = true }),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RectangleShape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = {
                    checked = it
                    onCheckedChange(it)
                                  },
                colors = CheckboxDefaults.colors(
                    checkedColor = LightBlue,
                    uncheckedColor = MutedTextColor
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = name, style = MaterialTheme.typography.bodyLarge, textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None)
        }
    }

}