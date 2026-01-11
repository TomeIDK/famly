package com.tome.famly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tome.famly.data.model.TaskList
import com.tome.famly.data.model.TaskListItem
import com.tome.famly.ui.components.TopBar
import com.tome.famly.ui.theme.BackgroundColor
import com.tome.famly.ui.theme.CustomOrange
import com.tome.famly.ui.theme.FamlyTheme
import com.tome.famly.ui.theme.LightBlue
import com.tome.famly.ui.viewmodels.TaskListViewModel

@Composable
fun TaskListScreen(taskListId: String, navController: NavController, viewModel: TaskListViewModel = viewModel()) {
    val list = viewModel.getTaskListById(taskListId)

    val listState = remember { mutableStateOf(list) }

    LaunchedEffect(taskListId) {
        if (listState.value == null) {
            listState.value = viewModel.fetchTaskListById(taskListId)
        }
        viewModel.loadLists()
    }

    val taskList = listState.value

    if (taskList == null) {
        Text("List not found")
        return
    }

    Scaffold(
        topBar = {
            TopBar(
                title = taskList.title,
                titleIcon = Icons.Outlined.CheckCircle,
                titleIconColor = CustomOrange,
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        TaskList(modifier = Modifier.padding(innerPadding), taskListId = taskList.id)
    }
}

@Composable
fun TaskList(modifier: Modifier = Modifier, taskListId: String, viewModel: TaskListViewModel = viewModel()) {
    val lists by viewModel.lists
    val taskList = lists.firstOrNull { it.id == taskListId }

    if (taskList == null) {
        Text("List not found")
        return
    }
    Column(
        modifier = modifier.fillMaxSize().background(BackgroundColor)
    ) {
        AddTaskListItemField(taskListId = taskList.id)
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
                    ItemsChecked(taskList.items.count { it.isChecked }, taskList.items.count())
                }
                items(taskList.items) { item ->
                    ListItem(
                        name = item.name,
                        isChecked = item.isChecked,
                        onCheckedChange = { newChecked -> viewModel.toggleTaskChecked(taskList.id, item.id, newChecked) },
                        onDelete = { viewModel.deleteTask(taskList.id, item.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun AddTaskListItemField(taskListId: String, viewModel: TaskListViewModel = viewModel()) {
    var text by remember { mutableStateOf("") }

    TextField(
        value = text,
        onValueChange = { text = it },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color(0xFFF2F2F2),
        ),
        placeholder = { Text("Add an item...") },
        trailingIcon = {
            IconButton(onClick = {
                if (text.isNotBlank()) {
                    viewModel.addItemToTaskList(taskListId, text)
                    text = ""
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = null, tint = LightBlue)
            }
        },
        singleLine = true
    )
}