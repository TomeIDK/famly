package com.tome.famly.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.tome.famly.ui.screens.FamilyEntryScreen
import com.tome.famly.ui.screens.Home
import com.tome.famly.ui.screens.LoginScreen
import com.tome.famly.ui.screens.MealPlannerScreen
import com.tome.famly.ui.screens.ShoppingListScreen
import com.tome.famly.ui.screens.ShoppingListsScreen
import com.tome.famly.ui.screens.TaskListScreen
import com.tome.famly.ui.screens.TasksListsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Login.name,
    ){
        // login
        composable(route = Routes.Login.name) {
            LoginScreen(navController)
        }

        // family entry
        composable(route = Routes.FamilyEntryScreen.name) {
            FamilyEntryScreen(navController)
        }

        // home
        composable(route = Routes.Home.name) {
            Home(navController)
        }

        // shopping lists
        composable(route = Routes.ShoppingLists.name) {
            ShoppingListsScreen(navController = navController)
        }

        // shopping list detail
        composable(route = "${Routes.ShoppingListDetail.name}/{shoppingListId}",
            arguments = listOf(navArgument("shoppingListId") { type = NavType.StringType })
        ) { backStackEntry ->
            val shoppingListId = backStackEntry.arguments?.getString("shoppingListId") ?: return@composable

            ShoppingListScreen(
                shoppingListId = shoppingListId,
                navController = navController,
            )
        }

        // task lists
        composable(route = Routes.TasksLists.name) {
            TasksListsScreen(navController = navController)
        }

        // task list detail
        composable(route = "${Routes.TaskListDetail.name}/{taskListId}",
            arguments = listOf(navArgument("taskListId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskListId = backStackEntry.arguments?.getString("taskListId") ?: return@composable

            TaskListScreen(
                taskListId = taskListId,
                navController = navController,
            )
        }

        // meal planner
        composable(route = Routes.MealPlanner.name) {
            MealPlannerScreen(navController = navController)
        }
    }
}