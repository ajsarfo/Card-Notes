package com.sarftec.simplenotes.presentation.message.note

import com.sarftec.simplenotes.entity.MetaNote

interface MessageBox {
    val note: MetaNote
    val type: Int

    companion object {
        const val NEW = 1
        const val UPDATE = 0
        const val NOTE_MESSAGE = "message"
    }
}