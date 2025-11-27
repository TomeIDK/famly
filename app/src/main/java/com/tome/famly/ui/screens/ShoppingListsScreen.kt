package com.tome.famly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tome.famly.data.mock.mockShoppingLists
import com.tome.famly.data.model.ShoppingList
import com.tome.famly.ui.components.TopBar
import com.tome.famly.ui.theme.BackgroundColor
import com.tome.famly.ui.theme.FamlyTheme
import com.tome.famly.ui.theme.LightBlue
import com.tome.famly.ui.theme.MutedTextColor
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

@Composable
fun ShoppingListsScreen(onBackClick: (() -> Unit)?, onShoppingListClick: (Int) -> Unit) {
    Scaffold(
        topBar = {
            TopBar(
                title = "Shopping Lists",
                titleIcon = Icons.Outlined.ShoppingCart,
                titleIconColor = LightBlue,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                containerColor = LightBlue,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        ShoppingLists( modifier = Modifier.padding(innerPadding), onShoppingListClick = onShoppingListClick)
    }
}

@Composable
fun ShoppingLists(modifier: Modifier = Modifier, onShoppingListClick: (Int) -> Unit) {
    LazyColumn(modifier = modifier.fillMaxSize().background(BackgroundColor)) {
        items(mockShoppingLists) { list ->
            ShoppingListCard(id = list.id, name = list.title, uncheckedItems = list.items.count { !it.isChecked }, onClick = onShoppingListClick)
        }
    }
}


@Composable
fun ShoppingListCard(id: Int, name: String, uncheckedItems: Int, onClick: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding()
            .clickable { onClick(id) },
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


@Preview(showBackground = true)
@Composable
fun ShoppingListsScreenPreview() {
    FamlyTheme {
        ShoppingListsScreen(onShoppingListClick = {}, onBackClick = {})
    }
}
