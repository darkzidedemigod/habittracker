package com.habitonix.data.repo

import com.habitonix.data.db.HabitCompletionDao
import com.habitonix.data.db.HabitCompletionEntity
import com.habitonix.data.db.HabitDao
import com.habitonix.data.db.HabitEntity
import com.habitonix.data.model.DayProgressSummary
import com.habitonix.data.model.HabitTodayItem
import com.habitonix.data.model.toEpochDayLong
import com.habitonix.data.model.toLocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao,
    private val completionDao: HabitCompletionDao,
) : HabitRepository {

    override fun observeHabits(): Flow<List<HabitEntity>> = habitDao.observeHabits()

    override fun observeActiveHabits(): Flow<List<HabitEntity>> = habitDao.observeActiveHabits()

    override fun observeToday(date: LocalDate): Flow<List<HabitTodayItem>> {
        val epochDay = date.toEpochDayLong()
        return combine(
            habitDao.observeActiveHabits(),
            completionDao.observeCompletionsForDate(epochDay),
        ) { habits, completions ->
            val completionByHabitId = completions.associateBy { it.habitId }
            buildList(habits.size) {
                for (habit in habits) {
                    val completed = completionByHabitId[habit.id]?.completed == true
                    val streak = computeStreakCount(habit.id, date)
                    add(
                        HabitTodayItem(
                            habit = habit,
                            date = date,
                            isCompleted = completed,
                            streakCount = streak,
                        ),
                    )
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    override fun observeDayProgress(date: LocalDate): Flow<DayProgressSummary> {
        val epochDay = date.toEpochDayLong()
        return combine(
            habitDao.observeActiveHabits(),
            completionDao.observeCompletionsForDate(epochDay),
        ) { habits, completions ->
            val completedSet = completions.filter { it.completed }.map { it.habitId }.toSet()
            DayProgressSummary(
                completed = habits.count { it.id in completedSet },
                total = habits.size,
            )
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun createHabit(title: String, description: String, icon: String?): Long {
        val today = LocalDate.now().toEpochDayLong()
        return habitDao.insert(
            HabitEntity(
                title = title.trim(),
                description = description.trim(),
                icon = icon?.trim()?.takeIf { it.isNotBlank() },
                createdDateEpochDay = today,
                isActive = true,
            ),
        )
    }

    override suspend fun updateHabit(habit: HabitEntity) {
        habitDao.update(habit.copy(title = habit.title.trim(), description = habit.description.trim()))
    }

    override suspend fun deleteHabit(habitId: Long) {
        val habit = habitDao.getById(habitId) ?: return
        habitDao.delete(habit)
    }

    override suspend fun setHabitActive(habitId: Long, isActive: Boolean) {
        val habit = habitDao.getById(habitId) ?: return
        habitDao.update(habit.copy(isActive = isActive))
    }

    override suspend fun setHabitCompleted(habitId: Long, date: LocalDate, completed: Boolean) {
        val epochDay = date.toEpochDayLong()
        val existing = completionDao.getCompletion(habitId, epochDay)
        val entity = HabitCompletionEntity(
            id = existing?.id ?: 0L,
            habitId = habitId,
            dateEpochDay = epochDay,
            completed = completed,
        )
        completionDao.upsert(entity)
    }

    override fun observeCompletionsInRange(
        start: LocalDate,
        endInclusive: LocalDate,
    ): Flow<Map<Long, Map<LocalDate, Boolean>>> {
        val startEpoch = start.toEpochDayLong()
        val endEpoch = endInclusive.toEpochDayLong()
        return completionDao.observeCompletionsInRange(startEpoch, endEpoch)
            .map { list ->
                list.groupBy { it.habitId }.mapValues { (_, entries) ->
                    entries.associate { it.dateEpochDay.toLocalDate() to it.completed }
                }
            }
            .flowOn(Dispatchers.IO)
    }

    private suspend fun computeStreakCount(habitId: Long, upTo: LocalDate): Int {
        return withContext(Dispatchers.IO) {
            val dates = completionDao.getCompletedDatesDesc(habitId, upTo.toEpochDayLong())
            if (dates.isEmpty()) return@withContext 0
            var streak = 0
            var cursor = upTo.toEpochDayLong()
            val set = dates.toHashSet()
            while (set.contains(cursor)) {
                streak++
                cursor -= 1
            }
            streak
        }
    }
}

