package com.sarftec.simplenotes.presentation.notification.todo

import com.sarftec.simplenotes.model.Todo

sealed class MainToTodo {
    class DeleteTodo(val todo: Todo) : MainToTodo()
    class Sort(val type: Int) : MainToTodo() {
        companion object {
            const val ASC = 0
            const val DESC = 1
            const val DATE = 3
        }
    }
    object CreateTodo : MainToTodo()
    object DeleteCompleted : MainToTodo()
    object Neutral: MainToTodo()
}