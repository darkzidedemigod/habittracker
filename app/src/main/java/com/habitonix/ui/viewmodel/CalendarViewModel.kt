package com.habitonix.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitonix.data.db.HabitEntity
import com.habitonix.data.repo.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

data class CalendarDaySummary(
    val date: LocalDate,
    val completedCount: Int,
    val missedCount: Int,
    val total: Int,
)

@HiltViewModel
class CalendarViewModel @Inject constructor(
    repo: HabitRepository,
) : ViewModel() {

    private val month: YearMonth = YearMonth.now()
    private val start = month.atDay(1)
    private val end = month.atEndOfMonth()

    val activeHabits: StateFlow<List<HabitEntity>> =
        repo.observeActiveHabits().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val completions =
        repo.observeCompletionsInRange(start, end)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    val days: StateFlow<List<CalendarDaySummary>> =
        combine(activeHabits, completions) { habits, completionMap ->
            val habitIds = habits.map { it.id }
            val total = habitIds.size
            (0 until month.lengthOfMonth()).map { i ->
                val date = start.plusDays(i.toLong())
                val completedForDay = habitIds.count { id -> completionMap[id]?.get(date) == true }
                val missedForDay = if (total == 0) 0 else total - completedForDay
                CalendarDaySummary(
                    date = date,
                    completedCount = completedForDay,
                    missedCount = missedForDay,
                    total = total,
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

