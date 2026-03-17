package com.habitonix.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val icon: String?,
    val createdDateEpochDay: Long,
    val isActive: Boolean,
)

