package com.sarftec.cardnotes.presentation.listener

import android.content.Intent
import com.sarftec.cardnotes.presentation.notification.note.NoteToMain

interface NoteListener {
    fun navigate(intent: Intent)
    fun notification(notification: NoteToMain)
}