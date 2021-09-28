package com.sarftec.simplenotes.data.repositoryimpl

import com.sarftec.simplenotes.data.AppDatabase
import com.sarftec.simplenotes.data.repository.TodoRepository
import com.sarftec.simplenotes.model.Todo
import javax.inject.Inject

class DiskTodoRepository @Inject constructor(
    private val appDatabase: AppDatabase
): TodoRepository {

    override suspend fun create(todo: Todo): Int {
       return appDatabase.todoDao().create(todo).toInt()
    }

    override suspend fun delete(id: Int) {
        return appDatabase.todoDao().delete(id)
    }

    override suspend fun delete(todos: List<Todo>) {
        appDatabase.todoDao().delete(todos)
    }

    override suspend fun update(todo: Todo) {
      return appDatabase.todoDao().update(todo)
    }

    override suspend fun todos(): List<Todo> {
      return appDatabase.todoDao().todos()
    }

    override suspend fun findTodo(id: Int): Todo? {
       return appDatabase.todoDao().find(id)
    }
}