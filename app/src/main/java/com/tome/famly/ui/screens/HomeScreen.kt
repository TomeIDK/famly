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
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tome.famly.ui.theme.LightBlue

class HomeScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FamlyTheme {
                Scaffold() { innerPadding ->
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
            .padding(10.dp)
    ) {
        TopBar()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color(0xFFFCFAF6)),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            SmallCard()
            SmallCard()
            SmallCard()
        }
        WideCard()
    }
}


@Composable
fun TopBar(currentFamily: String = "Famly Pas") {
    Row( modifier = Modifier
        .fillMaxWidth()
        .padding(9.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = currentFamily,
            style = MaterialTheme.typography.titleMedium
        )
        Icon(
            Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = Color(0xFF757575)
        )
    }
}

@Composable
fun SmallCard(number: Int = 5, bottomText: String = "To Buy") {
    OutlinedCard(
        modifier = Modifier.size(width = 100.dp, height = 120.dp)
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
                color = LightBlue

            )
            Text(
                text = bottomText,
                style = MaterialTheme.typography.labelMedium,
                color = LightBlue
            )
        }
    }
}

@Composable
fun WideCard(title: String = "Colruyt") {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Outlined.ShoppingCart,
                contentDescription = null,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x222062CE), shape = RectangleShape)
                    .padding(8.dp),
                tint = LightBlue,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text(text = "Shopping Lists",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold)
                Text(text = "$title ● 12 items left",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF757575))
                LinearProgressIndicator(
                    progress = { .7f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = LightBlue,
                )
            }
            Box(
                modifier = Modifier
                    .background(Color(0xFFFCFAF6), shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "3 active",
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
        Home()
    }
}