package com.sarftec.cardnotes.presentation.message.note

import com.sarftec.cardnotes.entity.MetaNote

interface MessageBox {
    val note: MetaNote
    val type: Int

    companion object {
        const val NEW = 1
        const val UPDATE = 0
        const val NOTE_MESSAGE = "message"
    }
}