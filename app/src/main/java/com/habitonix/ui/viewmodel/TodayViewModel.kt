package com.habitonix.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitonix.data.model.DayProgressSummary
import com.habitonix.data.model.HabitTodayItem
import com.habitonix.data.repo.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TodayViewModel @Inject constructor(
    private val repo: HabitRepository,
) : ViewModel() {

    private val today: LocalDate = LocalDate.now()

    val items: StateFlow<List<HabitTodayItem>> =
        repo.observeToday(today).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val progress: StateFlow<DayProgressSummary> =
        repo.observeDayProgress(today).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DayProgressSummary(0, 0))

    fun setCompleted(habitId: Long, completed: Boolean) {
        viewModelScope.launch {
            repo.setHabitCompleted(habitId, today, completed)
        }
    }
}

