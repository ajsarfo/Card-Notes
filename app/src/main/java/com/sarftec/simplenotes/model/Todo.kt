package com.sarftec.simplenotes.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

const val TODO_TABLE = "todo_table"
@Entity(tableName = TODO_TABLE)
@Parcelize
class Todo(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var title: String,
    val date: Long = Date().time,
    var isCompleted: Boolean = false,
    var shouldAlert: Boolean = false,
    var alertTime: Long = -1
) : Parcelable