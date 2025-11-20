package com.tome.famly.ui.screens


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tome.famly.R
import com.tome.famly.ui.theme.CustomOrange
import com.tome.famly.ui.theme.LightBlue

class HomeScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FamlyTheme {
                Scaffold(
                    topBar = {
                        TopBar()
                    }
                ) { innerPadding ->
                    Home( modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Home(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFCFAF6))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            SmallCard(number = 12, bottomText = "To Buy", color = LightBlue)
            SmallCard(number = 5, bottomText = "Chores Due", color = CustomOrange)
            SmallCard(number = 3, bottomText = "Meals Planned", color = LightBlue)
        }
        WideCard(title = "Shopping Lists", subtitle = "Colruyt", icon = Icons.Outlined.ShoppingCart, "3 active", color = LightBlue)
        WideCard(title = "Chores & Tasks", subtitle = "Feed the cat ● Clean kitchen", icon = Icons.Outlined.CheckCircle, "3 active", color = CustomOrange)
        WideCard(title = "Meal Planning", subtitle = "Tonight: Mongolian Beef", icon = ImageVector.vectorResource(R.drawable.outline_fork_spoon_24), "This week", color = LightBlue)
    }
}


@Composable
fun TopBar(currentFamily: String = "Pas Famly") {
    Row( modifier = Modifier
        .fillMaxWidth()
        .background(Color.White)
        .statusBarsPadding()
        .height(64.dp)
        .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = currentFamily,
            style = MaterialTheme.typography.titleMedium,
            fontSize = 18.sp
        )
        Icon(
            Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = Color(0xFF757575)
        )
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
                color = Color(0xFF757575),
                fontWeight = FontWeight.W400
            )
        }
    }
}

@Composable
fun WideCard(title: String, subtitle: String, icon: ImageVector, badgeText: String, color: Color) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
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
                    .padding(16.dp)
            ) {
                Text(text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold)
                Text(text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF757575))
                LinearProgressIndicator(
                    progress = { .7f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = color,
                )
            }
            Box(
                modifier = Modifier
                    .background(Color(0xFFFCFAF6), shape = RoundedCornerShape(12.dp))
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


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    FamlyTheme {
        Scaffold(topBar = { TopBar() }) {
            Home(Modifier.padding(it))
        }
    }
}