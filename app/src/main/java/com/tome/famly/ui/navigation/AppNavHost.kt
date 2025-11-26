package com.tome.famly.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tome.famly.ui.screens.Home
import com.tome.famly.ui.screens.ShoppingListsScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.Home.name,
        modifier = Modifier.fillMaxSize()
    ){
        composable(route = Routes.Home.name) {
            Home(onShoppingListsClick = { navController.navigate(Routes.ShoppingLists.name) })
        }
        composable(route = Routes.ShoppingLists.name) {
            ShoppingListsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}