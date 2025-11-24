package com.tome.famly.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tome.famly.ui.screens.Home
import com.tome.famly.ui.screens.ShoppingLists
import com.tome.famly.ui.screens.ShoppingListsScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.Home.name
    ){
        composable(route = Routes.Home.name) {
            Home(
                onShoppingListsClick = {
                    navController.navigate(Routes.ShoppingLists.name)
                }
            )
        }
        composable(route = Routes.ShoppingLists.name) {
            ShoppingListsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}