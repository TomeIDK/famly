package com.tome.famly.data.mock

import com.tome.famly.data.model.ResetInterval
import com.tome.famly.data.model.TaskList
import com.tome.famly.data.model.TaskListItem

val mockTasks = listOf(
    TaskList(
        id = 1,
        title = "Feed the cats",
        resetInterval = ResetInterval.DAILY,
        items = listOf(
            TaskListItem(id = 1, name = "Aerin"),
            TaskListItem(id = 2, name = "Vitta"),
            TaskListItem(id = 3, name = "Mingo")
        )
    ),
    TaskList(
        id = 2,
        title = "Weekly Chores",
        resetInterval = ResetInterval.WEEKLY,
        items = listOf(
            TaskListItem(id = 4, name = "Vacuum living room"),
            TaskListItem(id = 5, name = "Laundry")
        )
    ),
    TaskList(
        id = 3,
        title = "Morning Routine",
        resetInterval = ResetInterval.MONTHLY,
        items = listOf(
            TaskListItem(id = 1, name = "Make bed"),
            TaskListItem(id = 2, name = "Brush teeth", isChecked = true),
            TaskListItem(id = 3, name = "Feed pet")
        )
    )
)