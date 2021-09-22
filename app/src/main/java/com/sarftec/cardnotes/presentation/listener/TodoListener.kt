package com.sarftec.cardnotes.presentation.listener

import com.sarftec.cardnotes.model.Todo

interface TodoListener {
    fun createDeleteSheet(todo: Todo)
    fun createRemoveCompletedSheet()
    fun createTodoSheet()
}