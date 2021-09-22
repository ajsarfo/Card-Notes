package com.sarftec.cardnotes.presentation.notification.note

sealed class NoteToMain {
    class NoteCount(val count: Int) : NoteToMain()
    class Mode(val isNormal: Boolean) : NoteToMain()
}