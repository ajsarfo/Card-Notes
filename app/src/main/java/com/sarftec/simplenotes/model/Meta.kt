package com.sarftec.simplenotes.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

const val META_TABLE = "meta_table"
@Entity(
    tableName = META_TABLE,
    foreignKeys = [
        ForeignKey(
            entity = Note::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@Parcelize
class Meta(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(index = true) var noteId: Int = 0,
    var noteColor: Int
) : Parcelable