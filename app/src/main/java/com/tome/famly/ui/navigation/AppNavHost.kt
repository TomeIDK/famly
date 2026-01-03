package com.tome.famly.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.tome.famly.data.mock.mockShoppingLists
import com.tome.famly.data.mock.mockTasks
import com.tome.famly.data.model.Family
import com.tome.famly.data.model.ShoppingList
import com.tome.famly.data.model.TaskList
import com.tome.famly.ui.screens.Home
import com.tome.famly.ui.screens.MealPlannerScreen
import com.tome.famly.ui.screens.ShoppingListScreen
import com.tome.famly.ui.screens.ShoppingListsScreen
import com.tome.famly.ui.screens.TaskListScreen
import com.tome.famly.ui.screens.TasksListsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    userId: String,
    currentFamily: Family,
    onChangeFamily: (Family) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Home.name,
        modifier = Modifier.fillMaxSize(),
    ){
        composable(route = Routes.Home.name) {
            Home(
                onShoppingListsClick = { navController.navigate(Routes.ShoppingLists.name) },
                onTasksListsClick = { navController.navigate(Routes.TasksLists.name) },
                onMealPlannerClick = { navController.navigate(Routes.MealPlanner.name) },
                onSignOut = { FirebaseAuth.getInstance().signOut() },
                currentFamily = currentFamily,
                onChangeFamily = onChangeFamily,
                userId = userId
            )
        }
        composable(route = Routes.ShoppingLists.name) {
            ShoppingListsScreen(
                onBackClick = { navController.popBackStack() },
                onShoppingListClick = { shoppingListId -> navController.navigate("${Routes.ShoppingListDetail.name}/$shoppingListId") }
            )
        }
        composable(route = "${Routes.ShoppingListDetail.name}/{shoppingListId}",
            arguments = listOf(navArgument("shoppingListId") { type = NavType.IntType })
        ) { backStackEntry ->
            val shoppingListId = backStackEntry.arguments?.getInt("shoppingListId") ?: 0
            val shoppingList: ShoppingList? = mockShoppingLists.find { it.id == shoppingListId }

            shoppingList?.let {
                ShoppingListScreen(shoppingList = shoppingList, onBackClick = {
                    navController.popBackStack()
                })
            }
        }
        composable(route = Routes.TasksLists.name) {
            TasksListsScreen(
                onBackClick = { navController.popBackStack() },
                onTaskListClick = { taskId -> navController.navigate("${Routes.TaskListDetail.name}/$taskId")}
            )
        }
        composable(route = "${Routes.TaskListDetail.name}/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: 0
            val task: TaskList? = mockTasks.find { it.id == taskId }

            task?.let {
                TaskListScreen(taskList = it, onBackClick = {
                    navController.popBackStack()
                })
            }
        }
        composable(route = Routes.MealPlanner.name) {
            MealPlannerScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}