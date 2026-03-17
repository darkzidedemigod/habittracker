package com.habitonix.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits ORDER BY createdDateEpochDay DESC, id DESC")
    fun observeHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE isActive = 1 ORDER BY createdDateEpochDay DESC, id DESC")
    fun observeActiveHabits(): Flow<List<HabitEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(habit: HabitEntity): Long

    @Update
    suspend fun update(habit: HabitEntity)

    @Delete
    suspend fun delete(habit: HabitEntity)

    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getById(id: Long): HabitEntity?
}

