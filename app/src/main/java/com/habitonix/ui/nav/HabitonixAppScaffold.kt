package com.habitonix.ui.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.habitonix.R
import com.habitonix.ui.screens.calendar.CalendarScreen
import com.habitonix.ui.screens.habits.HabitsScreen
import com.habitonix.ui.screens.today.TodayScreen
import com.habitonix.ui.theme.HabitonixTheme

@Composable
fun HabitonixAppScaffold() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    HabitonixAppScaffoldContent(
        currentDestination = currentDestination,
        onNavigateToDestination = { dest ->
            navController.navigate(dest.route) {
                popUpTo(BottomDestination.Today.route) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = BottomDestination.Today.route,
            modifier = Modifier.padding(padding)
                .padding(WindowInsets.navigationBars.asPaddingValues()),
        ) {
            composable(BottomDestination.Today.route) { TodayScreen() }
            composable(BottomDestination.Calendar.route) { CalendarScreen() }
            composable(BottomDestination.Habits.route) { HabitsScreen() }
        }
    }
}

@Composable
private fun HabitonixAppScaffoldContent(
    currentDestination: NavDestination?,
    onNavigateToDestination: (BottomDestination) -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            NavigationBar(
                containerColor = colorResource(R.color.primary), // Change to your desired color
            ) {
                bottomDestinations.forEach { dest ->
                    val selected = currentDestination
                        ?.hierarchy
                        ?.any { it.route == dest.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = { onNavigateToDestination(dest) },
                        icon = {
                            androidx.compose.material3.Icon(
                                painter = rememberVectorPainter(dest.icon),
                                contentDescription = dest.label,
                            )
                        },
                        label = { Text(dest.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedTextColor = colorResource(R.color.accent), // Set the color for selected text
                            unselectedTextColor = colorResource(R.color.dark_gray), // Set the color for unselected text
                            selectedIconColor = colorResource(R.color.primary), // Optional: customize icon colors as well
                            unselectedIconColor = colorResource(R.color.dark_gray),
                            indicatorColor = colorResource(R.color.accent) // Optional: customize the indicator color
                        )
                    )
                }
            }
        },
        content = content,
    )
}

@Preview(showBackground = true)
@Composable
private fun HabitonixAppScaffoldPreview() {
    HabitonixTheme {
        HabitonixAppScaffoldContent(
            currentDestination = null,
            onNavigateToDestination = {},
        ) { padding ->
            Box(Modifier.padding(padding).fillMaxSize()) {
                Text("App Content Area", Modifier.padding(16.dp))
            }
        }
    }
}
