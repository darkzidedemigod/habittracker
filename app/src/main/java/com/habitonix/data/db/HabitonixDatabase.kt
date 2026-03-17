package com.habitonix.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        HabitEntity::class,
        HabitCompletionEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class HabitonixDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun habitCompletionDao(): HabitCompletionDao
}

