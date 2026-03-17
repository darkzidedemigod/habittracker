package com.habitonix.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Tune
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    data object Today : BottomDestination("today", "Today", Icons.Filled.Checklist)
    data object Calendar : BottomDestination("calendar", "Calendar", Icons.Filled.CalendarMonth)
    data object Habits : BottomDestination("habits", "Habits", Icons.Filled.Tune)
}

val bottomDestinations = listOf(
    BottomDestination.Today,
    BottomDestination.Calendar,
    BottomDestination.Habits,
)

