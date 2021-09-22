package com.sarftec.cardnotes.presentation.message.note.request

import com.sarftec.cardnotes.entity.MetaNote
import com.sarftec.cardnotes.presentation.message.note.MessageBox
import com.sarftec.cardnotes.presentation.message.note.response.Response

abstract class Request(override val note: MetaNote, override val type: Int) : MessageBox {
    abstract fun toResponse(code: Int) : Response
}