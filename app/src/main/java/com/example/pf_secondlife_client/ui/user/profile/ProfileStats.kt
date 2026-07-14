package com.example.pf_secondlife_client.ui.user.profile

import com.example.pf_secondlife_client.domain.models.GetPostResponse
import com.example.pf_secondlife_client.domain.valuesSets.ArticleType
import com.example.pf_secondlife_client.domain.valuesSets.PostState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TimeSeriesPoint(val label: String, val count: Int)

data class ProfileStats(
    val totalPosts: Int,
    val totalSold: Int,
    val soldPercentage: Float,
    val postsOverTime: List<TimeSeriesPoint>,
    val salesOverTime: List<TimeSeriesPoint>,
)

private val monthFormatter = SimpleDateFormat("MM/yy", Locale.getDefault())

private fun monthLabel(timestamp: Long): String = monthFormatter.format(Date(timestamp))

fun computeProfileStats(posts: List<GetPostResponse>, filter: ArticleType?): ProfileStats {
    val filtered = if (filter == null) posts else posts.filter { it.type == filter }

    val total = filtered.size
    val sold = filtered.count { it.state == PostState.SOLD }
    val soldPercentage = if (total == 0) 0f else (sold.toFloat() / total) * 100f

    val postsOverTime = filtered
        .groupBy { monthLabel(it.timestamp) }
        .toSortedMap()
        .map { (label, group) -> TimeSeriesPoint(label, group.size) }

    val salesOverTime = filtered
        .filter { it.state == PostState.SOLD }
        .groupBy { monthLabel(it.timestamp) }
        .toSortedMap()
        .map { (label, group) -> TimeSeriesPoint(label, group.size) }

    return ProfileStats(total, sold, soldPercentage, postsOverTime, salesOverTime)
}