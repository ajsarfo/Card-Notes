package com.sarftec.simplenotes.presentation.notification.note

sealed class NoteToMain {
    class NoteCount(val count: Int) : NoteToMain()
    class Mode(val isNormal: Boolean) : NoteToMain()
}