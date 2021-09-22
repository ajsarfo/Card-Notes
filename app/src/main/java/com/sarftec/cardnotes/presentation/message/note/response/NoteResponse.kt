package com.sarftec.cardnotes.presentation.message.note.response

import android.os.Parcelable
import com.sarftec.cardnotes.entity.MetaNote
import kotlinx.parcelize.Parcelize

sealed class NoteResponse(
    note: MetaNote,
    type: Int,
    code: Int
) : Response(note, type, code) {

    @Parcelize
    class New(
        override val note: MetaNote,
        override val type: Int,
        override val code: Int
    ) : NoteResponse(note, type, code), Parcelable

    @Parcelize
    class Update(
        override val note: MetaNote,
        override val type: Int,
        val position: Int,
        override val code: Int,
    ) : NoteResponse(note, type, code), Parcelable
}