package com.habitonix.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.habitonix.R
import com.habitonix.data.db.HabitEntity
import com.habitonix.ui.viewmodel.CalendarDaySummary
import com.habitonix.ui.viewmodel.CalendarViewModel
import java.time.format.TextStyle
import java.util.Locale
import com.habitonix.ui.theme.HabitonixTheme
import java.time.LocalDate

@Composable
fun CalendarScreen(
    vm: CalendarViewModel = hiltViewModel(),
) {
    val days by vm.days.collectAsStateWithLifecycle()
    val habits by vm.activeHabits.collectAsStateWithLifecycle()
    val completions by vm.completions.collectAsStateWithLifecycle()

    CalendarScreenContent(
        days = days,
        habits = habits,
        completions = completions,
    )
}

@Composable
fun CalendarScreenContent(
    days: List<CalendarDaySummary>,
    habits: List<HabitEntity>,
    completions: Map<Long, Map<LocalDate, Boolean>>,
) {
    var selected by remember { mutableStateOf<CalendarDaySummary?>(null) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val monthLabel = days.firstOrNull()?.date?.month?.getDisplayName(TextStyle.FULL, Locale.getDefault())
                ?: "Calendar"

            Text(
                text = monthLabel,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = colorResource(R.color.primary)
            )
            Spacer(Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                contentPadding = PaddingValues(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(days, key = { it.date.toEpochDay() }) { day ->
                    DayCell(
                        day = day,
                        onClick = { selected = day },
                    )
                }
            }
        }
    }

    selected?.let { day ->
        val lines = habits.map { habit ->
            val completed = completions[habit.id]?.get(day.date) == true
            val marker = if (completed) "✓" else "–"
            "$marker ${habit.icon?.let { "$it " } ?: ""}${habit.title}"
        }

        AlertDialog(
            onDismissRequest = { selected = null },
            title = { Text(day.date.toString()) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("${day.completedCount} completed • ${day.missedCount} missed")
                    Spacer(Modifier.height(6.dp))
                    lines.forEach { Text(it, style = MaterialTheme.typography.bodyMedium) }
                }
            },
            confirmButton = {
                TextButton(onClick = { selected = null }) { Text("Close") }
            },
        )
    }
}

@Composable
private fun DayCell(
    day: CalendarDaySummary,
    onClick: () -> Unit,
) {
    val hasHabits = day.total > 0
    val completedAll = hasHabits && day.completedCount == day.total
    val anyCompleted = day.completedCount > 0

    val container =
        when {
            completedAll -> MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
            anyCompleted -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f)
            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        }

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = container,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = day.date.dayOfMonth.toString(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
            if (hasHabits) {
                CompletionDots(completed = day.completedCount, missed = day.missedCount)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CompletionDots(
    completed: Int,
    missed: Int,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        maxItemsInEachRow = 6,
    ) {
        repeat(minOf(completed, 6)) {
            Dot(MaterialTheme.colorScheme.primary)
        }
        repeat(minOf(missed, maxOf(0, 6 - minOf(completed, 6)))) {
            Dot(MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun Dot(color: androidx.compose.ui.graphics.Color) {
    Spacer(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(color),
    )
}

@Preview(showBackground = true)
@Composable
private fun CalendarScreenPreview() {
    val date = LocalDate.now()
    val habits = listOf(
        HabitEntity(1, "Drink water", "8 glasses", "💧", date.toEpochDay(), true),
    )
    val days = (0..30).map {
        CalendarDaySummary(date.minusDays(it.toLong()), completedCount = it % 2, missedCount = (it + 1) % 2, total = 1)
    }
    HabitonixTheme {
        CalendarScreenContent(
            days = days,
            habits = habits,
            completions = mapOf(1L to days.associate { it.date to (it.completedCount > 0) })
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CalendarCellPreview() {
    HabitonixTheme {
        Column(Modifier.padding(16.dp)) {
            DayCell(
                day = CalendarDaySummary(LocalDate.now(), completedCount = 3, missedCount = 2, total = 5),
                onClick = {},
            )
        }
    }
}
