package com.habitonix.di

import android.content.Context
import androidx.room.Room
import com.habitonix.data.db.HabitCompletionDao
import com.habitonix.data.db.HabitDao
import com.habitonix.data.db.HabitonixDatabase
import com.habitonix.data.repo.HabitRepository
import com.habitonix.data.repo.HabitRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    @Singleton
    abstract fun bindHabitRepository(impl: HabitRepositoryImpl): HabitRepository

    companion object {
        @Provides
        @Singleton
        fun provideDatabase(
            @ApplicationContext context: Context,
        ): HabitonixDatabase {
            return Room.databaseBuilder(
                context,
                HabitonixDatabase::class.java,
                "habitonix.db",
            ).build()
        }

        @Provides
        fun provideHabitDao(db: HabitonixDatabase): HabitDao = db.habitDao()

        @Provides
        fun provideHabitCompletionDao(db: HabitonixDatabase): HabitCompletionDao = db.habitCompletionDao()
    }
}

