package com.sarftec.cardnotes.entity

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import com.sarftec.cardnotes.model.Meta
import com.sarftec.cardnotes.model.Note
import kotlinx.parcelize.Parcelize

@Parcelize
class MetaNote(
    @Embedded
    val note: Note,
    @Relation(
        parentColumn = "id",
        entityColumn = "noteId"
    )
    val meta: Meta
) : Parcelable