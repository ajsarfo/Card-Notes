package com.sarftec.simplenotes.entity

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import com.sarftec.simplenotes.model.Meta
import com.sarftec.simplenotes.model.Note
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