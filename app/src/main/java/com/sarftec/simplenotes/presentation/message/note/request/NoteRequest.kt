package com.sarftec.simplenotes.presentation.message.note.request

import android.os.Parcelable
import com.sarftec.simplenotes.entity.MetaNote
import com.sarftec.simplenotes.presentation.message.note.response.NoteResponse
import com.sarftec.simplenotes.presentation.message.note.response.Response
import kotlinx.parcelize.Parcelize

sealed class NoteRequest(note: MetaNote, type: Int) : Request(note, type) {

    @Parcelize
    class New(
        override val note: MetaNote,
        override val type: Int
    ) : NoteRequest(note, type), Parcelable {
        override fun toResponse(code: Int): Response = NoteResponse.New(note, type, code)
    }

    @Parcelize
    class Update(
        override val note: MetaNote,
        override val type: Int,
        val position: Int
    ) : NoteRequest(note, type), Parcelable {
        override fun toResponse(code: Int): Response =
            NoteResponse.Update(note, type, position, code)
    }
}