package com.sarftec.simplenotes.data.repository

import com.sarftec.simplenotes.entity.TodoNotify

interface TodoNotifyRepository {
    suspend fun addTodoNotify(todoNotify: TodoNotify)
    suspend fun removeTodoNotify(todoId: Int)
    suspend fun findTodoNotify(todoId: Int) : TodoNotify?
    suspend fun todoNotifies() : List<TodoNotify>
    suspend fun updateTodoNotify(todoNotify: TodoNotify)
}