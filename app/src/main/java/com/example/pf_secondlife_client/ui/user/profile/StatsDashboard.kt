package com.example.pf_secondlife_client.ui.user.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StatsDashboard(stats: ProfileStats) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Stats", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                StatBox(label = "Posts", value = stats.totalPosts.toString(), modifier = Modifier.weight(1f))
                StatBox(label = "Sold", value = stats.totalSold.toString(), modifier = Modifier.weight(1f))
                StatBox(label = "% Sold", value = "${stats.soldPercentage.toInt()}%", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Posts over time", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
            SimpleBarChart(points = stats.postsOverTime, barColor = MaterialTheme.colorScheme.primary)

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Sells over time", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
            SimpleBarChart(points = stats.salesOverTime, barColor = MaterialTheme.colorScheme.tertiary)
        }
    }
}

@Composable
private fun StatBox(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = value, style = MaterialTheme.typography.headlineSmall)
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SimpleBarChart(points: List<TimeSeriesPoint>, barColor: Color) {
    if (points.isEmpty()) {
        Text(
            text = "No data",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        return
    }

    val maxCount = points.maxOf { it.count }.coerceAtLeast(1)

    Row(modifier = Modifier.fillMaxWidth().height(120.dp)) {
        points.forEach { point ->
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
            ) {
                Canvas(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                ) {
                    val barHeightRatio = point.count.toFloat() / maxCount
                    val barHeight = size.height * barHeightRatio
                    drawRect(
                        color = barColor,
                        topLeft = Offset(0f, size.height - barHeight),
                        size = Size(size.width, barHeight),
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = point.label, style = MaterialTheme.typography.labelSmall)
                Text(text = point.count.toString(), style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}