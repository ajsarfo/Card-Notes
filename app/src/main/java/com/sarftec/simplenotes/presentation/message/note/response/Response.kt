package com.sarftec.simplenotes.presentation.message.note.response

import com.sarftec.simplenotes.entity.MetaNote
import com.sarftec.simplenotes.presentation.message.note.MessageBox

abstract class Response(
    override val note: MetaNote,
    override val type: Int,
    open val code: Int
    ) : MessageBox {

    companion object {
        const val ERROR = 0
        const val OK = 1
    }
}