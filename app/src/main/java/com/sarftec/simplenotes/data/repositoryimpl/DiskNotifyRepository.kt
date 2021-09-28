package com.sarftec.simplenotes.data.repositoryimpl

import com.sarftec.simplenotes.data.AppDatabase
import com.sarftec.simplenotes.data.repository.TodoNotifyRepository
import com.sarftec.simplenotes.entity.TodoNotify
import javax.inject.Inject

class DiskNotifyRepository @Inject constructor(
    private val appDatabase: AppDatabase
): TodoNotifyRepository {

    override suspend fun addTodoNotify(todoNotify: TodoNotify) {
        appDatabase.notifyDao().addNotify(todoNotify)
    }

    override suspend fun removeTodoNotify(todoId: Int) {
        appDatabase.notifyDao().removeNotify(todoId)
    }

    override suspend fun findTodoNotify(todoId: Int): TodoNotify? {
        return appDatabase.notifyDao().findNotify(todoId)
    }

    override suspend fun todoNotifies(): List<TodoNotify> {
        return appDatabase.notifyDao().notifies()
    }

    override suspend fun updateTodoNotify(todoNotify: TodoNotify) {
        appDatabase.notifyDao().updateNotify(todoNotify)
    }
}