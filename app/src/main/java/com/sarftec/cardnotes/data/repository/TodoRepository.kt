package com.sarftec.cardnotes.data.repository

import com.sarftec.cardnotes.model.Todo

interface TodoRepository {
    suspend fun create(todo: Todo) : Int
    suspend fun delete(id: Int)
    suspend fun delete(todos: List<Todo>)
    suspend fun update(todo: Todo)
    suspend fun todos() : List<Todo>
    suspend fun findTodo(id: Int) : Todo?
}