package com.sarftec.simplenotes.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

const val NOTIFY_TABLE = "notify_table"

@Entity(tableName = NOTIFY_TABLE)
@Parcelize
class TodoNotify(
    @PrimaryKey(autoGenerate = false) var todoId: Int,
    var time: Long
) : Parcelable