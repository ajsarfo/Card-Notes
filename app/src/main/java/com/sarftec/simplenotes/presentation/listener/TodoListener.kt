package com.sarftec.simplenotes.presentation.listener

import com.sarftec.simplenotes.model.Todo

interface TodoListener {
    fun createDeleteSheet(todo: Todo)
    fun createRemoveCompletedSheet()
    fun createTodoSheet()
}