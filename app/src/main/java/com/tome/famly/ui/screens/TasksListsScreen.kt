package com.tome.famly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tome.famly.ui.components.TopBar
import com.tome.famly.ui.theme.BackgroundColor
import com.tome.famly.ui.theme.CustomOrange
import com.tome.famly.ui.theme.FamlyTheme
import com.tome.famly.ui.theme.MutedTextColor
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.hours

@Composable
fun TasksListsScreen(onBackClick: (() -> Unit)?) {
    Scaffold(
        topBar = {
            TopBar(
                title = "Task Lists",
                titleIcon = Icons.Outlined.CheckCircle,
                titleIconColor = CustomOrange,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                containerColor = CustomOrange,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        TaskLists( modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun TaskLists(modifier: Modifier = Modifier){
    LazyColumn(modifier = modifier.fillMaxSize().background(BackgroundColor)) {
        items(10) {
            TaskCard("Feed the cats", 2, Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.plus(1,
                DateTimeUnit.DAY).atStartOfDayIn(TimeZone.currentSystemDefault()))
        }
        item {
            TaskCard("Weekly House Chores", 0, Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.plus(1,
                DateTimeUnit.DAY).atStartOfDayIn(TimeZone.currentSystemDefault()))

        }
    }
}

@Composable
fun TaskCard(name: String, items: Int, resetDateTime: Instant) {
    val now = Clock.System.now()
    val diff = resetDateTime - now
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RectangleShape
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Outlined.CheckCircle,
                contentDescription = null,
                modifier = Modifier
                    .padding(8.dp),
                tint = randomColor(),
            )
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W500
                )

                Row {
                    Icon(
                        Icons.Filled.DateRange,
                        contentDescription = null,
                        tint = MutedTextColor,
                        modifier = Modifier.size(16.dp)
                        )
                    Text(
                        text = if (diff < 24.hours) {
                            val hours = diff.inWholeHours
                            "Resets in $hours hours"
                        } else {
                            val day = resetDateTime
                                .toLocalDateTime(TimeZone.currentSystemDefault())
                                .date
                                .dayOfWeek
                                .name
                            "Resets on $day"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedTextColor,
                        modifier = Modifier.padding(start = 2.dp)

                    )
                }
            }

            Spacer(Modifier.weight(1f))
            if (items > 0) {
                Box(
                    modifier = Modifier
                        .background(MutedTextColor.copy(alpha = 0.6f), shape = CircleShape)
                        .padding(vertical = 2.dp, horizontal = 10.dp),
                ) {
                    Text(
                        text = items.toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .background(CustomOrange, shape = CircleShape)
                        .padding(vertical = 2.dp, horizontal = 2.dp),
                ) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                    )
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun TasksListsScreen() {
    FamlyTheme {
        TasksListsScreen(onBackClick = {})
    }
}