package com.sarftec.cardnotes.presentation.notify

import com.sarftec.cardnotes.data.repository.TodoNotifyRepository
import com.sarftec.cardnotes.data.repository.TodoRepository
import com.sarftec.cardnotes.entity.TodoNotify
import javax.inject.Inject

class NotifyManager @Inject constructor(
    private val alarmReceiver: NotifyAlarmMaker,
    private val todoRepository: TodoRepository,
    private val todoNotifyRepository: TodoNotifyRepository
) {

    suspend fun addTodoNotify(todoNotify: TodoNotify) {
        if (todoNotifyRepository.todoNotifies().isEmpty()) {
            alarmReceiver.startAlarm(todoNotify)
        } else {
            todoNotifyRepository.todoNotifies()
                .minByOrNull { it.time }
                ?.takeIf { todoNotify.time < it.time }
                ?.let { restartAlarm(todoNotify) }
        }
        todoNotifyRepository.addTodoNotify(todoNotify)
    }

    suspend fun updateTodoNotify(todoNotify: TodoNotify) {
        todoNotifyRepository.updateTodoNotify(todoNotify)
        todoNotifyRepository.todoNotifies()
            .minByOrNull { it.time }
            ?.takeIf { todoNotify.time < it.time }
            ?.let { restartAlarm(todoNotify) }
    }

    suspend fun removeTodoNotify(todoId: Int) {
        todoNotifyRepository.removeTodoNotify(todoId)
        todoNotifyRepository.todoNotifies()
            .minByOrNull { it.time }
            ?.let { restartAlarm(it) }
        todoRepository.findTodo(todoId)?.let {
                //it.shouldAlert = false
                todoRepository.update(it)
            }
    }

    private fun restartAlarm(todoNotify: TodoNotify) {
        alarmReceiver.stopAlarm()
        alarmReceiver.startAlarm(todoNotify)
    }
}