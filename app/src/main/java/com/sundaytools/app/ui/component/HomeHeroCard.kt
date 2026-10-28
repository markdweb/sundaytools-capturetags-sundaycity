package com.sundaytools.app.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Calendar

/**
 * Greeting hero card shown at the top of the Home screen: mascot, a
 * time-of-day greeting, and today's saved-tag count as the headline stat.
 */
@Composable
fun HomeHeroCard(todayCount: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B4332))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SundayMascot(size = 72.dp)
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = greetingForNow(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = "$todayCount tag${if (todayCount == 1) "" else "s"} saved today",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFFCDE8D9)
                )
            }
        }
    }
}

private fun greetingForNow(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Good morning!"
        hour < 18 -> "Good afternoon!"
        else -> "Good evening!"
    }
}
