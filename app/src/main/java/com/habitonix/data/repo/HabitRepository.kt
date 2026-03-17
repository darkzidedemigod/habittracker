package com.habitonix.data.repo

import com.habitonix.data.db.HabitEntity
import com.habitonix.data.model.DayProgressSummary
import com.habitonix.data.model.HabitTodayItem
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface HabitRepository {
    fun observeHabits(): Flow<List<HabitEntity>>
    fun observeActiveHabits(): Flow<List<HabitEntity>>

    fun observeToday(date: LocalDate): Flow<List<HabitTodayItem>>
    fun observeDayProgress(date: LocalDate): Flow<DayProgressSummary>

    suspend fun createHabit(
        title: String,
        description: String,
        icon: String?,
    ): Long

    suspend fun updateHabit(habit: HabitEntity)
    suspend fun deleteHabit(habitId: Long)
    suspend fun setHabitActive(habitId: Long, isActive: Boolean)

    suspend fun setHabitCompleted(habitId: Long, date: LocalDate, completed: Boolean)

    fun observeCompletionsInRange(
        start: LocalDate,
        endInclusive: LocalDate,
    ): Flow<Map<Long, Map<LocalDate, Boolean>>>
}

