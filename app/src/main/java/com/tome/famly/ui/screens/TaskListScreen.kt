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
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tome.famly.data.mock.mockTasks
import com.tome.famly.data.model.TaskList
import com.tome.famly.ui.components.TopBar
import com.tome.famly.ui.theme.BackgroundColor
import com.tome.famly.ui.theme.CustomOrange
import com.tome.famly.ui.theme.FamlyTheme

@Composable
fun TaskListScreen(taskList: TaskList, onBackClick: (() -> Unit)?) {
    Scaffold(
        topBar = {
            TopBar(
                title = taskList.title,
                titleIcon = Icons.Outlined.CheckCircle,
                titleIconColor = CustomOrange,
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        TaskList( modifier = Modifier.padding(innerPadding), taskList = taskList)
    }
}

@Composable
fun TaskList(modifier: Modifier = Modifier, taskList: TaskList) {
    Column(
        modifier = modifier.fillMaxSize().background(BackgroundColor)
    ) {
        AddItemField()
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
                    ListItem(name = item.name, checked = item.isChecked, onCheckedChange = {})
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaskListScreenPreview() {
    FamlyTheme {
        TaskListScreen(taskList = mockTasks[0], onBackClick = {})
    }
}