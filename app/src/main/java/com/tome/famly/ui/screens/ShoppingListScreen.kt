package com.tome.famly.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tome.famly.data.model.ShoppingList
import com.tome.famly.ui.components.TopBar
import com.tome.famly.ui.theme.BackgroundColor
import com.tome.famly.ui.theme.FamlyTheme
import com.tome.famly.ui.theme.LightBlue
import com.tome.famly.ui.theme.MutedTextColor
import com.tome.famly.data.mock.mockShoppingLists
import com.tome.famly.data.model.ShoppingListItem

@Composable
fun ShoppingListScreen(shoppingList: ShoppingList, onBackClick: (() -> Unit)?) {
    Scaffold(
        topBar = {
            TopBar(
                title = shoppingList.title,
                titleIcon = Icons.Outlined.ShoppingCart,
                titleIconColor = LightBlue,
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        ShoppingList( modifier = Modifier.padding(innerPadding), shoppingList = shoppingList)
    }
}

@Composable
fun ShoppingList(modifier: Modifier = Modifier, shoppingList: ShoppingList) {
    val items = remember { mutableStateListOf(*shoppingList.items.toTypedArray()) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        AddItemField(items)
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
                    ItemsChecked(items.count { it.isChecked.value }, items.count())
                }
                items(items) { item ->
                    ListItem(name = item.name, checked = item.isChecked.value, onCheckedChange = { item.isChecked.value = it })
                }
            }
        }
    }
}

@Composable
fun AddItemField(list: MutableList<ShoppingListItem>) {
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
                list.add(ShoppingListItem(id = list.size + 1, name = text))
                text = ""
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

@Composable
fun ListItem(name: String, checked: Boolean = false, onCheckedChange: (Boolean) -> Unit) {
    Card(
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
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = LightBlue,
                    uncheckedColor = MutedTextColor
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = name, style = MaterialTheme.typography.bodyLarge, textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None)
        }
    }

}

@Preview(showBackground = true)
@Composable
fun ShoppingListScreenPreview() {
    FamlyTheme {
        ShoppingListScreen(shoppingList = mockShoppingLists[0], onBackClick = {})
    }
}