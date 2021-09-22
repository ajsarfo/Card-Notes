package com.sarftec.cardnotes.data.repository

import com.sarftec.cardnotes.entity.TodoNotify

interface TodoNotifyRepository {
    suspend fun addTodoNotify(todoNotify: TodoNotify)
    suspend fun removeTodoNotify(todoId: Int)
    suspend fun findTodoNotify(todoId: Int) : TodoNotify?
    suspend fun todoNotifies() : List<TodoNotify>
    suspend fun updateTodoNotify(todoNotify: TodoNotify)
}