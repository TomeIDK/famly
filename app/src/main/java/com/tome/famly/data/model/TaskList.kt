package com.tome.famly.data.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.Timestamp
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

data class TaskList(
    val id: String,
    val title: String,
    val resetInterval: ResetInterval = ResetInterval.WEEKLY,
    val items: List<TaskListItem>,
    val createdAt: Timestamp

) {
    fun nextResetDateTimeLocal(): LocalDateTime {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val nextDate = when (resetInterval) {
            ResetInterval.DAILY -> now.plus(1, DateTimeUnit.DAY)
            ResetInterval.WEEKLY -> {
                val daysUntilSunday = ((7 - now.dayOfWeek.isoDayNumber % 7).takeIf { true } ?: 7)
                now.plus(daysUntilSunday.toLong(), DateTimeUnit.DAY)
            }
            ResetInterval.MONTHLY -> now.plus(1, DateTimeUnit.MONTH)
        }

        return nextDate.atStartOfDayIn(TimeZone.currentSystemDefault()).toLocalDateTime(TimeZone.currentSystemDefault())
    }
}

data class TaskListItem(
    val id: String,
    val name: String,
    var isChecked: Boolean = false
)

enum class ResetInterval{
    DAILY, WEEKLY, MONTHLY
}