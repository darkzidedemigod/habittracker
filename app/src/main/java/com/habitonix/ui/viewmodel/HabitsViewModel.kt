package com.habitonix.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitonix.data.db.HabitEntity
import com.habitonix.data.repo.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitsViewModel @Inject constructor(
    private val repo: HabitRepository,
) : ViewModel() {

    val habits: StateFlow<List<HabitEntity>> =
        repo.observeHabits().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun createHabit(title: String, description: String, icon: String?) {
        viewModelScope.launch { repo.createHabit(title, description, icon) }
    }

    fun updateHabit(habit: HabitEntity) {
        viewModelScope.launch { repo.updateHabit(habit) }
    }

    fun deleteHabit(habitId: Long) {
        viewModelScope.launch { repo.deleteHabit(habitId) }
    }

    fun setActive(habitId: Long, isActive: Boolean) {
        viewModelScope.launch { repo.setHabitActive(habitId, isActive) }
    }
}

