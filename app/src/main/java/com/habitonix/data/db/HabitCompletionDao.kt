package com.habitonix.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitCompletionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: HabitCompletionEntity): Long

    @Query("SELECT * FROM habit_completions WHERE dateEpochDay = :dateEpochDay")
    fun observeCompletionsForDate(dateEpochDay: Long): Flow<List<HabitCompletionEntity>>

    @Query(
        """
        SELECT * FROM habit_completions
        WHERE dateEpochDay BETWEEN :startEpochDay AND :endEpochDay
        """
    )
    fun observeCompletionsInRange(startEpochDay: Long, endEpochDay: Long): Flow<List<HabitCompletionEntity>>

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId AND dateEpochDay = :dateEpochDay LIMIT 1")
    suspend fun getCompletion(habitId: Long, dateEpochDay: Long): HabitCompletionEntity?

    @Query(
        """
        SELECT dateEpochDay
        FROM habit_completions
        WHERE habitId = :habitId
          AND completed = 1
          AND dateEpochDay <= :upToEpochDay
        ORDER BY dateEpochDay DESC
        """
    )
    suspend fun getCompletedDatesDesc(habitId: Long, upToEpochDay: Long): List<Long>

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId")
    suspend fun deleteAllForHabit(habitId: Long)
}

