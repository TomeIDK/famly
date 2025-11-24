package com.tome.famly.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.tome.famly.ui.components.TopBar
import com.tome.famly.ui.theme.FamlyTheme
import com.tome.famly.ui.theme.LightBlue

@Composable
fun ShoppingListScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopBar(
                title = "Colruyt",
                titleIcon = Icons.Outlined.ShoppingCart,
                titleIconColor = LightBlue,
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        ShoppingList( modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun ShoppingList(modifier: Modifier = Modifier) {

}

@Preview(showBackground = true)
@Composable
fun ShoppingListScreenPreview() {
    FamlyTheme {
        ShoppingListScreen(onBackClick = {})
    }
}