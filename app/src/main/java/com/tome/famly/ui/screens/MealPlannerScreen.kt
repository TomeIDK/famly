package com.tome.famly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tome.famly.R
import com.tome.famly.data.mock.mockMealPlans
import com.tome.famly.data.mock.mockRecipes
import com.tome.famly.data.model.MealPlan
import com.tome.famly.data.model.MealPlannerTab
import com.tome.famly.data.model.Recipe
import com.tome.famly.ui.components.TopBar
import com.tome.famly.ui.theme.BackgroundColor
import com.tome.famly.ui.theme.FamlyTheme
import com.tome.famly.ui.theme.LightBlue
import com.tome.famly.ui.theme.MutedTextColor
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.plus

fun getCurrentWeekDates(): List<LocalDate> {
    val today = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date

    val dayIndex = today.dayOfWeek.ordinal
    val monday = today.minus(DatePeriod(days = dayIndex))

    return (0..6).map { i ->
        monday.plus(DatePeriod(days = i))
    }
}

fun LocalDate.toDisplayString(): String {
    val monthNames = listOf(
        "Jan","Feb","Mar","Apr","May","Jun",
        "Jul","Aug","Sep","Oct","Nov","Dec"
    )
    val dayNames = listOf(
        "Monday","Tuesday","Wednesday","Thursday",
        "Friday","Saturday","Sunday"
    )

    val dayName = dayNames[this.dayOfWeek.ordinal]
    val monthName = monthNames[this.monthNumber - 1]

    return "$dayName, $monthName ${this.dayOfMonth}"
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlannerScreen(onBackClick: () -> Unit) {
    var selectedTab by remember { mutableStateOf(MealPlannerTab.WEEKPLAN) }
    var showAddRecipeBottomSheet by remember { mutableStateOf(false) }
    var showChangeMealPlanBottomSheet by remember { mutableStateOf(false) }
    var selectedDateForChange by remember { mutableStateOf<LocalDate?>(null) }

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
                onClick = { showAddRecipeBottomSheet = true },
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
                MealPlannerTab.WEEKPLAN -> MealPlanner(
                    modifier = Modifier.padding(innerPadding),
                    onChangeClick = { date ->
                    selectedDateForChange = date
                    showChangeMealPlanBottomSheet = true
                })
                MealPlannerTab.RECIPES -> RecipeBook(modifier = Modifier.padding(innerPadding))
            }
        }

        if (showAddRecipeBottomSheet) {
            AddRecipeBottomSheet(
                onDismiss = { showAddRecipeBottomSheet = false },
                onSave = { newRecipe ->
                    mockRecipes.add(newRecipe)
                }
            )
        }
        
        if (showChangeMealPlanBottomSheet) {
            ChangeMealPlanBottomSheet(
                onDismiss = { showChangeMealPlanBottomSheet = false },
                onSave = { selectedRecipe ->
                    selectedDateForChange?.let { date ->
                        val plan = mockMealPlans.find { it.date == selectedDateForChange }
                        if (plan != null) {
                            plan.recipe.value = selectedRecipe?.title
                        } else {
                            mockMealPlans.add(MealPlan(selectedDateForChange!!, mutableStateOf(selectedRecipe?.title)))
                        }

                        selectedDateForChange = null
                    }
                    showChangeMealPlanBottomSheet = false
                }
            )
        }

    }
}

@Composable
fun MealPlanner(modifier: Modifier = Modifier, onChangeClick: (LocalDate) -> Unit) {
    val weekDates = getCurrentWeekDates()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(weekDates.size) { index ->
            val date = weekDates[index]
            val planForDay = mockMealPlans.find { it.date == date }
                ?: MealPlan(date, mutableStateOf((null)))

            MealPlanCard(mealPlan = planForDay, onChangeClick = { onChangeClick(date) })
        }

    }
}

@Composable
fun MealPlanCard(mealPlan: MealPlan, onChangeClick: () -> Unit) {
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
                text = mealPlan.date.toDisplayString(),
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
                Text(
                    text = mealPlan.recipe.value ?: "No meal planned",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp
                )
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
                    .clickable(onClick = { onChangeClick() })
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecipeBottomSheet(
    onDismiss: () -> Unit,
    onSave: (Recipe) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().drawBehind {
                    val strokeWidth = 1.dp.toPx()
                    val y = size.height - strokeWidth / 2
                    drawLine(
                        color = Color.Gray,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = strokeWidth
                    )
                }.padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,

                ) {
                Text(text = "New Recipe", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Icon(Icons.Outlined.Close, contentDescription = "Close", modifier = Modifier.clickable { onDismiss() })
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Column {
                    Text("Recipe Title", style = MaterialTheme.typography.labelLarge)
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Chicken Katsu Curry") },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            unfocusedTextColor = MutedTextColor,
                            focusedLabelColor = LightBlue,
                            focusedIndicatorColor = LightBlue,
                        )
                    )
                }

                Column {
                    Text("Recipe Description", style = MaterialTheme.typography.labelLarge)
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Ingredients, instructions,...") },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            unfocusedTextColor = MutedTextColor,
                            focusedLabelColor = LightBlue,
                            focusedIndicatorColor = LightBlue,
                        )
                    )
                }

                Column {
                    Text("Recipe Link", style = MaterialTheme.typography.labelLarge)
                    OutlinedTextField(
                        value = link,
                        onValueChange = { link = it },
                        label = { Text("Paste a link to your recipe here...") },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            unfocusedTextColor = MutedTextColor,
                            focusedLabelColor = LightBlue,
                            focusedIndicatorColor = LightBlue,
                        )
                    )
                }

                Button(onClick = {
                    val recipe = Recipe(
                        title = title,
                        description = desc,
                        link = link
                    )
                    onSave(recipe)
                    onDismiss()
                },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LightBlue
                    )
                ) {
                    Text("Create Recipe", style = MaterialTheme.typography.titleMedium)
                }

            }


        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeMealPlanBottomSheet(
    onDismiss: () -> Unit,
    onSave: (Recipe?) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().drawBehind {
                    val strokeWidth = 1.dp.toPx()
                    val y = size.height - strokeWidth / 2
                    drawLine(
                        color = Color.Gray,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = strokeWidth
                    )
                }.padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,

                ) {
                Text(text = "Change Meal Plan", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Icon(Icons.Outlined.Close, contentDescription = "Close", modifier = Modifier.clickable { onDismiss() })
            }

            Button(
                onClick = { onSave(null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Clear Meal Plan")
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(mockRecipes.size) { index ->
                    ChangeMealPlanRecipeCard(recipe = mockRecipes[index], onClick = { onSave(mockRecipes[index]) })
                }
            }
        }
    }
}

@Composable
fun ChangeMealPlanRecipeCard(
    recipe: Recipe,
    onClick: () -> Unit
) {

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = recipe.title,
                style = MaterialTheme.typography.titleMedium
            )

            recipe.description?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedTextColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
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