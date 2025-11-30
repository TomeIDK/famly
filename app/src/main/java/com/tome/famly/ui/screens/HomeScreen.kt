package com.tome.famly.ui.screens


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.tome.famly.ui.theme.FamlyTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.tome.famly.R
import com.tome.famly.data.mock.mockShoppingLists
import com.tome.famly.data.mock.mockTasks
import com.tome.famly.ui.navigation.AppNavHost
import com.tome.famly.ui.theme.BackgroundColor
import com.tome.famly.ui.theme.CustomOrange
import com.tome.famly.ui.theme.LightBlue
import com.tome.famly.ui.theme.MutedTextColor
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


class HomeScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FamlyTheme {
                val navController = rememberNavController()

                AppNavHost(navController = navController)
            }
        }
    }
}

@Composable
fun Home(
    modifier: Modifier = Modifier,
    onShoppingListsClick: () -> Unit,
    onTasksListsClick: () -> Unit,
    onMealPlannerClick: () -> Unit,
    ) {

    val toBuy: Int = mockShoppingLists.sumOf { shoppingList ->  shoppingList.items.count { !it.isChecked.value } }
    val choresDue: Int = mockTasks.sumOf { taskList ->  taskList.items.count { !it.isChecked.value } }
    val firstShoppingListWithUnchecked = mockShoppingLists.firstOrNull { list ->
        list.items.any { !it.isChecked.value }
    }
    val firstTaskListWithUnchecked = mockTasks.firstOrNull { list ->
        list.items.any { !it.isChecked.value }
    }

    Scaffold(
        topBar = {
            HomeTopBar()
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(BackgroundColor)
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Top Cards
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    SmallCard(number = toBuy, bottomText = "To Buy", color = LightBlue)
                    SmallCard(number = choresDue, bottomText = "Chores Due", color = CustomOrange)
                    SmallCard(number = 3, bottomText = "Meals Planned", color = LightBlue)
                }
            }


            // Shopping Lists
            item {
                WideCard(
                    title = "Shopping Lists",
                    subtitle = if (firstShoppingListWithUnchecked != null) "${firstShoppingListWithUnchecked.title} ● ${firstShoppingListWithUnchecked.items.count { !it.isChecked.value }} items left"
                    else "All shopping lists completed!",
                    progress = (mockShoppingLists.sumOf { shoppingList ->  shoppingList.items.count { it.isChecked.value } }.toFloat() / mockShoppingLists.sumOf { shoppingList ->  shoppingList.items.count() }.toFloat()),
                    icon = Icons.Outlined.ShoppingCart,
                    "${mockShoppingLists.size} active",
                    color = LightBlue,
                    onClick = onShoppingListsClick
                )
            }
            // Tasks
            item {
                WideCard(
                    title = "Chores & Tasks",
                    subtitle = if (firstTaskListWithUnchecked != null) "${firstTaskListWithUnchecked.title} ● ${firstTaskListWithUnchecked.items.count { !it.isChecked.value }} tasks left"
                    else "All tasks completed!",
                    progress = (mockTasks.sumOf { taskList ->  taskList.items.count { it.isChecked.value } }.toFloat() / mockTasks.sumOf { taskList ->  taskList.items.count() }.toFloat()),
                    icon = Icons.Outlined.CheckCircle,
                    "${mockTasks.size} active",
                    color = CustomOrange,
                    onClick = onTasksListsClick
                )
            }
            // Meal Planning
            item {
                MealPlanningCard(
                    subtitle = "Tonight: Mongolian Beef",
                    onClick = onMealPlannerClick
                )
            }

            // Family Members
            item {
                Text(
                    "FAMILY MEMBERS",
                    style = MaterialTheme.typography.titleMedium,
                    color = MutedTextColor
                )
            }

            items(
                listOf(
                    "Cedric Pas" to "Admin",
                    "Sandra Pas" to "Member",
                    "Filip Pas" to "Member",
                    "Angels Dubois" to "Member",
                    "Angels Dubois" to "Member",
                    "Angels Dubois" to "Member",
                    "Angels Dubois" to "Member",
                )
            ) { (name, role) ->
                FamilyMemberCard(
                    name,
                    email = "${name.lowercase().replace(" ", "")}@gmail.com",
                    role = role
                )
            }
        }
    }
}

@Composable
fun SmallCard(number: Int, bottomText: String, color: Color) {
    OutlinedCard(
        modifier = Modifier.size(width = 100.dp, height = 120.dp),
        elevation = CardDefaults.outlinedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = number.toString(),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Medium,
                color = color

            )
            Text(
                text = bottomText,
                style = MaterialTheme.typography.labelMedium,
                color = MutedTextColor,
                fontWeight = FontWeight.W400
            )
        }
    }
}

@Composable
fun WideCard(title: String, subtitle: String, progress: Float, icon: ImageVector, badgeText: String, color: Color, onClick: () -> Unit = {}) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.outlinedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.2f), shape = RectangleShape)
                    .padding(8.dp),
                tint = color,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold)
                Text(text = subtitle,
                    modifier = Modifier.padding(top = 4.dp, bottom = 6.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedTextColor)
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = color,
                )
            }
            Box(
                modifier = Modifier
                    .background(BackgroundColor, shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = badgeText,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                )
            }
        }

    }
}

@Composable
fun MealPlanningCard(subtitle: String, onClick: () -> Unit = {}) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.outlinedCardElevation(defaultElevation = 2.dp)
    ) {
        Column {


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
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
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Meal Planning",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        modifier = Modifier.padding(top = 4.dp, bottom = 6.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedTextColor
                    )
                }
                Box(
                    modifier = Modifier
                        .background(BackgroundColor, shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "This week",
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                }
            }
            WeekdayRow()
        }

    }
}

@Composable
fun WeekdayRow(
    modifier: Modifier = Modifier
) {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    val today = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
        .dayOfWeek
        .ordinal

    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        days.forEachIndexed { index, day ->
            val isToday = index == today

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 12.dp)
                    .background(
                        color = if (isToday) LightBlue else MutedTextColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day,
                    color = if (isToday) Color.White else Color.Black,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun FamilyMemberCard(name: String, email: String, role: String) {
    val badgeColor = if (role == "Admin") LightBlue else BackgroundColor
    val badgeTextColor = if (role == "Admin") Color.White else Color.Black
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = name, style = MaterialTheme.typography.titleMedium, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedTextColor
                )
            }
            Spacer(Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .background(badgeColor, shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    text = role,
                    color = badgeTextColor,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun HomeTopBar(currentFamily: String = "Pas Famly") {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp)
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
            .height(56.dp)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = currentFamily,
            style = MaterialTheme.typography.titleMedium,
            fontSize = 18.sp
        )
        Icon(
            Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = MutedTextColor
        )
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    FamlyTheme {
        Scaffold(topBar = { HomeTopBar() }) {
            Home(Modifier.padding(it), onShoppingListsClick = {}, onTasksListsClick = {}, onMealPlannerClick = {})
        }
    }
}
