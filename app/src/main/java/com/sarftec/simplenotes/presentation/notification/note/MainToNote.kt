package com.sarftec.simplenotes.presentation.notification.note

sealed class MainToNote {
    class CheckAll(val checked: Boolean) : MainToNote()
    object DeleteNotes : MainToNote()
    object CloseDelete : MainToNote()
    object Neutral : MainToNote()
}