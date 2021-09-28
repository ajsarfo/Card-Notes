package com.sarftec.simplenotes.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

const val NOTE_TABLE = "note_table"
@Entity(tableName = NOTE_TABLE)
@Parcelize
data class Note(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var title: String,
    var content: String,
    var date: Long = Date().time
) : Parcelable