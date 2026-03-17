package com.habitonix.ui.screens.habits

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.habitonix.R
import com.habitonix.data.db.HabitEntity
import com.habitonix.ui.theme.HabitonixTheme
import com.habitonix.ui.viewmodel.HabitsViewModel
import java.time.LocalDate

@Composable
fun HabitsScreen(
    vm: HabitsViewModel = hiltViewModel(),
) {
    val habits by vm.habits.collectAsStateWithLifecycle()

    HabitsScreenContent(
        habits = habits,
        onCreate = vm::createHabit,
        onUpdate = vm::updateHabit,
        onDelete = vm::deleteHabit,
        onSetActive = vm::setActive,
    )
}

@Composable
fun HabitsScreenContent(
    habits: List<HabitEntity>,
    onCreate: (title: String, description: String, icon: String?) -> Unit,
    onUpdate: (HabitEntity) -> Unit,
    onDelete: (habitId: Long) -> Unit,
    onSetActive: (habitId: Long, isActive: Boolean) -> Unit,
) {
    var editorState by remember { mutableStateOf<HabitEditorState?>(null) }
    var pendingDelete by remember { mutableStateOf<HabitEntity?>(null) }

    Scaffold(
        containerColor = colorResource(R.color.background),
        floatingActionButton = {
            FloatingActionButton(onClick = { editorState = HabitEditorState.Create },
                containerColor = colorResource(R.color.primary),
                contentColor = colorResource(R.color.accent),) {
                Icon(Icons.Filled.Add, contentDescription = "Add habit")
            }
        },
    ) { padding ->
        // Changed Surface color to Transparent so it doesn't block the Scaffold background
        Surface(
            modifier = Modifier.fillMaxSize().padding(padding),
            color = Color.Transparent
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text(
                    text = "Habits",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(R.color.primary)
                )
                Spacer(Modifier.height(12.dp))
                Divider()
                Spacer(Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(habits, key = { it.id }) { habit ->
                        Surface(
                            shape = MaterialTheme.shapes.large,
                            tonalElevation = 1.dp,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = habit.icon?.takeIf { it.isNotBlank() } ?: "•",
                                    style = MaterialTheme.typography.titleLarge,
                                )
                                Column(modifier = Modifier.weight(1f).padding(start = 10.dp)) {
                                    Text(
                                        text = habit.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Medium,
                                    )
                                    if (habit.description.isNotBlank()) {
                                        Text(
                                            text = habit.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                                Switch(
                                    checked = habit.isActive,
                                    onCheckedChange = { checked -> onSetActive(habit.id, checked) },
                                )
                                IconButton(onClick = { editorState = HabitEditorState.Edit(habit) }) {
                                    Icon(Icons.Filled.Edit, contentDescription = "Edit")
                                }
                                IconButton(onClick = { pendingDelete = habit }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    HabitEditorDialog(
        state = editorState,
        onDismiss = { editorState = null },
        onSaveCreate = { title, description, icon ->
            onCreate(title, description, icon)
            editorState = null
        },
        onSaveUpdate = { habit ->
            onUpdate(habit)
            editorState = null
        },
    )

    pendingDelete?.let { habit ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Delete habit?") },
            text = { Text("This will remove the habit and its history.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(habit.id)
                        pendingDelete = null
                    },
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text("Cancel") }
            },
        )
    }
}

private sealed class HabitEditorState {
    data object Create : HabitEditorState()
    data class Edit(val habit: HabitEntity) : HabitEditorState()
}

@Composable
private fun HabitEditorDialog(
    state: HabitEditorState?,
    onDismiss: () -> Unit,
    onSaveCreate: (title: String, description: String, icon: String?) -> Unit,
    onSaveUpdate: (HabitEntity) -> Unit,
) {
    if (state == null) return

    val editing = (state as? HabitEditorState.Edit)?.habit
    var title by remember(state) { mutableStateOf(editing?.title ?: "") }
    var description by remember(state) { mutableStateOf(editing?.description ?: "") }
    var icon by remember(state) { mutableStateOf(editing?.icon ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (editing == null) "Add habit" else "Edit habit") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = icon,
                    onValueChange = { icon = it },
                    label = { Text("Icon (optional, e.g. ✅)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = title.trim().isNotBlank(),
                onClick = {
                    if (editing == null) {
                        onSaveCreate(title, description, icon.takeIf { it.isNotBlank() })
                    } else {
                        onSaveUpdate(editing.copy(title = title, description = description, icon = icon.takeIf { it.isNotBlank() }))
                    }
                },
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun HabitsScreenPreview() {
    val date = LocalDate.now()
    val habits = listOf(
        HabitEntity(1, "Drink water", "8 glasses", "💧", date.toEpochDay(), true),
        HabitEntity(2, "Read", "10 pages", "📚", date.toEpochDay(), true),
        HabitEntity(3, "No sugar", "Stay clean today", "🚫", date.toEpochDay(), false),
    )
    HabitonixTheme {
        HabitsScreenContent(
            habits = habits,
            onCreate = { _, _, _ -> },
            onUpdate = { },
            onDelete = { },
            onSetActive = { _, _ -> },
        )
    }
}
