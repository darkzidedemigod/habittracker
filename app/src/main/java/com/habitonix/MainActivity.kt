package com.habitonix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.habitonix.notifications.ReminderScheduler
import com.habitonix.ui.nav.HabitonixAppScaffold
import com.habitonix.ui.theme.HabitonixTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var reminderScheduler: ReminderScheduler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        reminderScheduler.ensureDailyReminderScheduled()

        setContent{
            HabitonixTheme {
                HabitonixAppScaffold()
            }
        }
    }
}

