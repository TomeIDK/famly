package com.tome.famly.data.model

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

data class TaskList(
    val id: Int,
    val title: String,
    val resetInterval: ResetInterval,
    val items: List<TaskListItem>
) {
    fun nextResetDateTimeLocal(): LocalDateTime {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val nextDate = when (resetInterval) {
            ResetInterval.DAILY -> now.plus(1, DateTimeUnit.DAY)
            ResetInterval.WEEKLY -> now.plus(7, DateTimeUnit.DAY)
            ResetInterval.MONTHLY -> now.plus(1, DateTimeUnit.MONTH)
            ResetInterval.CUSTOM -> now
        }

        return nextDate.atStartOfDayIn(TimeZone.currentSystemDefault()).toLocalDateTime(TimeZone.currentSystemDefault())
    }
}

data class TaskListItem(
    val id: Int,
    val name: String,
    var isChecked: Boolean = false
)

enum class ResetInterval{
    DAILY, WEEKLY, MONTHLY, CUSTOM
}