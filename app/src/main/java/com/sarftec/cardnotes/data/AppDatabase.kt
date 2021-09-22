package com.sarftec.cardnotes.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sarftec.cardnotes.data.dao.MetaDao
import com.sarftec.cardnotes.data.dao.NoteDao
import com.sarftec.cardnotes.data.dao.NotifyDao
import com.sarftec.cardnotes.data.dao.TodoDao
import com.sarftec.cardnotes.entity.TodoNotify
import com.sarftec.cardnotes.model.Meta
import com.sarftec.cardnotes.model.Note
import com.sarftec.cardnotes.model.Todo

@Database(entities = [Todo::class, TodoNotify::class, Note::class, Meta::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao() : TodoDao
    abstract fun notifyDao() : NotifyDao
    abstract fun noteDao() : NoteDao
    abstract fun metaDao() : MetaDao
}