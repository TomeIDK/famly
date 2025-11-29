package com.tome.famly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tome.famly.R
import com.tome.famly.data.model.MealPlannerTab
import com.tome.famly.ui.components.TopBar
import com.tome.famly.ui.theme.BackgroundColor
import com.tome.famly.ui.theme.FamlyTheme
import com.tome.famly.ui.theme.LightBlue
import com.tome.famly.ui.theme.MutedTextColor
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.plus

fun getCurrentWeekDates(): List<String> {
    val today = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date

    val dayIndex = today.dayOfWeek.ordinal
    val monday = today.minus(DatePeriod(days = dayIndex))

    val monthNames = listOf(
        "Jan","Feb","Mar","Apr","May","Jun",
        "Jul","Aug","Sep","Oct","Nov","Dec"
    )
    val dayNames = listOf("Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday")

    return (0..6).map { i ->
        val date = monday.plus(DatePeriod(days = i))
        val dayName = dayNames[date.dayOfWeek.ordinal]
        val monthName = monthNames[date.monthNumber - 1]
        "$dayName, $monthName ${date.dayOfMonth}"
    }
}

@Composable
fun MealPlannerScreen(onBackClick: () -> Unit) {
    var selectedTab by remember { mutableStateOf(MealPlannerTab.WEEKPLAN) }

    Scaffold(
        topBar = {
            TopBar(
                title = "Meal Planner",
                titleIcon = ImageVector.vectorResource(R.drawable.outline_fork_spoon_24),
                titleIconColor = LightBlue,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                containerColor = LightBlue,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(top = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MealPlannerTabSelector(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
            when (selectedTab) {
                MealPlannerTab.WEEKPLAN -> MealPlanner(modifier = Modifier.padding(innerPadding))
                MealPlannerTab.RECIPES -> RecipeBook(modifier = Modifier.padding(innerPadding))
            }
        }

    }
}

@Composable
fun MealPlanner(modifier: Modifier = Modifier) {
    val weekDays = getCurrentWeekDates()
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(weekDays.size) { index ->
            val date = weekDays[index]
            MealPlanCard("Mongolian Beef", date)
        }

    }
}

@Composable
fun MealPlanCard(mealTitle: String, date: String) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth()
            .padding(8.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(8.dp),
            )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 12.dp, start = 16.dp, end = 16.dp)
        ) {
            Text(
                text = date,
                color = MutedTextColor,
                style = MaterialTheme.typography.bodySmall,
            )
            Row(
                modifier = Modifier.padding(top = 6.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.outline_fork_spoon_24),
                    contentDescription = null,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(LightBlue.copy(alpha = 0.2f), shape = RectangleShape)
                        .padding(8.dp),
                    tint = LightBlue,
                )
                Column {
                    Text(
                        text = mealTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            Modifier.size(18.dp),
                            tint = MutedTextColor
                        )
                        Text(
                            text = "Everyone attending",
                            color = MutedTextColor,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .border(
                        width = 1.dp,
                        color = MutedTextColor,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .background(BackgroundColor)
                    .clickable(onClick = {})
                    .padding(vertical = 4.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Change",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )
            }
        }


    }
}

@Composable
fun MealPlannerTabSelector(
    selectedTab: MealPlannerTab,
    onTabSelected: (MealPlannerTab) -> Unit
) {
    val tabs = listOf("Meal Planner" to MealPlannerTab.WEEKPLAN, "Recipe Book" to MealPlannerTab.RECIPES)

    Row(
        modifier = Modifier
            .padding(8.dp)
            .border(width = 1.dp, color = MutedTextColor, shape = RoundedCornerShape(8.dp))
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tabs.forEach { (label, tab) ->
            val isSelected = tab == selectedTab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .background(
                        color = if (isSelected) LightBlue else MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .clickable { onTabSelected(tab) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MutedTextColor,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MealPlannerScreenPreview() {
    FamlyTheme {
        MealPlannerScreen(onBackClick = {})
    }
}