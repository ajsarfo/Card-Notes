package com.sarftec.cardnotes.data

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class StaticInjection {

    @Provides
    fun database(@ApplicationContext context: Context) : AppDatabase {
       return Room.databaseBuilder(
           context,
           AppDatabase::class.java,
           "app_database"
       ).build()
    }
}