package com.habitonix.data.model

import com.habitonix.data.db.HabitEntity
import java.time.LocalDate

data class HabitTodayItem(
    val habit: HabitEntity,
    val date: LocalDate,
    val isCompleted: Boolean,
    val streakCount: Int,
)

data class DayProgressSummary(
    val completed: Int,
    val total: Int,
) {
    val label: String get() = "$completed of $total habits completed"
}

