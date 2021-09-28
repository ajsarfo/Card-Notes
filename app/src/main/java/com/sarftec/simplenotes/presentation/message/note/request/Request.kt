package com.sarftec.simplenotes.presentation.message.note.request

import com.sarftec.simplenotes.entity.MetaNote
import com.sarftec.simplenotes.presentation.message.note.MessageBox
import com.sarftec.simplenotes.presentation.message.note.response.Response

abstract class Request(override val note: MetaNote, override val type: Int) : MessageBox {
    abstract fun toResponse(code: Int) : Response
}