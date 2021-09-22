package com.sarftec.cardnotes.data.testrepositoryimpl

import com.sarftec.cardnotes.data.repository.TodoNotifyRepository
import com.sarftec.cardnotes.entity.TodoNotify
import javax.inject.Inject

class TestTodoNotifyRepository @Inject constructor(

) : TodoNotifyRepository {

    private val todoNotifies = mutableListOf<TodoNotify>()

    override suspend fun addTodoNotify(todoNotify: TodoNotify) {
        todoNotifies.add(todoNotify)
    }

    override suspend fun removeTodoNotify(todoId: Int) {
        todoNotifies
            .firstOrNull { todoId == it.todoId }
            ?.let { todoNotifies.remove(it) }
    }

    override suspend fun findTodoNotify(todoId: Int): TodoNotify? {
        return todoNotifies.firstOrNull { it.todoId == todoId }
    }

    override suspend fun todoNotifies(): List<TodoNotify> {
        return todoNotifies
    }

    override suspend fun updateTodoNotify(todoNotify: TodoNotify) {

    }

    companion object {
        var ID = 0
    }
}