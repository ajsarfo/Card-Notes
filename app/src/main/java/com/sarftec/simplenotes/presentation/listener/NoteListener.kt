package com.sarftec.simplenotes.presentation.listener

import android.content.Intent
import com.sarftec.simplenotes.presentation.notification.note.NoteToMain

interface NoteListener {
    fun navigate(intent: Intent)
    fun notification(notification: NoteToMain)
}