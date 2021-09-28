package com.sarftec.simplenotes.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sarftec.simplenotes.data.dao.MetaDao
import com.sarftec.simplenotes.data.dao.NoteDao
import com.sarftec.simplenotes.data.dao.NotifyDao
import com.sarftec.simplenotes.data.dao.TodoDao
import com.sarftec.simplenotes.entity.TodoNotify
import com.sarftec.simplenotes.model.Meta
import com.sarftec.simplenotes.model.Note
import com.sarftec.simplenotes.model.Todo

@Database(
    entities = [Todo::class, TodoNotify::class, Note::class, Meta::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    abstract fun notifyDao(): NotifyDao
    abstract fun noteDao(): NoteDao
    abstract fun metaDao(): MetaDao

    companion object {
        fun getInstance(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
                .build()
        }
    }
}