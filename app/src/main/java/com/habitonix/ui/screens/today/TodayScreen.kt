package com.habitonix.ui.screens.today

import android.R
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.tooling.preview.Preview
import com.habitonix.data.db.HabitEntity
import com.habitonix.data.model.DayProgressSummary
import com.habitonix.data.model.HabitTodayItem
import com.habitonix.ui.theme.OrangeStreak
import com.habitonix.ui.theme.HabitonixTheme
import com.habitonix.ui.viewmodel.TodayViewModel
import java.time.LocalDate

@Composable
fun TodayScreen(
    vm: TodayViewModel = hiltViewModel(),
) {
    val items by vm.items.collectAsStateWithLifecycle()
    val progress by vm.progress.collectAsStateWithLifecycle()

    TodayScreenContent(
        items = items,
        progress = progress,
        onSetCompleted = { habitId, completed -> vm.setCompleted(habitId, completed) },
    )
}

@Composable
fun TodayScreenContent(
    items: List<HabitTodayItem>,
    progress: DayProgressSummary,
    onSetCompleted: (habitId: Long, completed: Boolean) -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = "Today",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = colorResource(com.habitonix.R.color.primary)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = progress.label,
                style = MaterialTheme.typography.bodyMedium,
                color = colorResource(com.habitonix.R.color.secondary),
            )
            Spacer(Modifier.height(12.dp))
            Divider()
            Spacer(Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(items, key = { it.habit.id }) { item ->
                    val bg by animateColorAsState(
                        targetValue = if (item.isCompleted) MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
                        else MaterialTheme.colorScheme.surface,
                        label = "habit-bg",
                    )

                    Surface(
                        tonalElevation = if (item.isCompleted) 2.dp else 0.dp,
                        shape = MaterialTheme.shapes.large,
                        color = bg,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Checkbox(
                                checked = item.isCompleted,
                                onCheckedChange = { checked -> onSetCompleted(item.habit.id, checked) },
                            )
                            Column(modifier = Modifier.weight(1f).padding(start = 6.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (!item.habit.icon.isNullOrBlank()) {
                                        Text(text = item.habit.icon!!, style = MaterialTheme.typography.titleMedium)
                                        Spacer(Modifier.width(6.dp))
                                    }
                                    Text(
                                        text = item.habit.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Medium,
                                    )
                                }
                                if (item.habit.description.isNotBlank()) {
                                    Text(
                                        text = item.habit.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LocalFireDepartment,
                                    contentDescription = "Streak",
                                    tint = OrangeStreak,
                                )
                                Text(
                                    text = item.streakCount.toString(),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TodayScreenPreview() {
    val date = LocalDate.now()
    val habits = listOf(
        HabitEntity(id = 1, title = "Drink water", description = "8 glasses", icon = "💧", createdDateEpochDay = date.toEpochDay(), isActive = true),
        HabitEntity(id = 2, title = "Read", description = "10 pages", icon = "📚", createdDateEpochDay = date.toEpochDay(), isActive = true),
        HabitEntity(id = 3, title = "Walk", description = "20 minutes", icon = "🚶", createdDateEpochDay = date.toEpochDay(), isActive = true),
    )
    val items = listOf(
        HabitTodayItem(habits[0], date, isCompleted = true, streakCount = 5),
        HabitTodayItem(habits[1], date, isCompleted = false, streakCount = 2),
        HabitTodayItem(habits[2], date, isCompleted = true, streakCount = 12),
    )
    HabitonixTheme {
        TodayScreenContent(
            items = items,
            progress = DayProgressSummary(completed = 2, total = 3),
            onSetCompleted = { _, _ -> },
        )
    }
}

