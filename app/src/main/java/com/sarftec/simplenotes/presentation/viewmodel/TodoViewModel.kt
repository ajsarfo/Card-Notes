package com.sarftec.simplenotes.presentation.viewmodel

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarftec.simplenotes.data.repository.TodoNotifyRepository
import com.sarftec.simplenotes.data.repository.TodoRepository
import com.sarftec.simplenotes.entity.TodoNotify
import com.sarftec.simplenotes.model.Todo
import com.sarftec.simplenotes.presentation.notify.NotifyManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class Todos(var todos: MutableList<Todo>) {
    private var id = -1

    override fun equals(other: Any?): Boolean {
        if (other !is NoteState) return false
        return other.id == this.id
    }

    fun switch() {
        id = if (id == -1) 0 else -1
    }
}

sealed class SheetTodo(val todo: Todo, var canSave: Boolean = false) {
    private var id = 0

    class New(todo: Todo) : SheetTodo(todo)
    class Update(todo: Todo) : SheetTodo(todo)
    object Neutral : SheetTodo(Todo(title = ""))

    override fun equals(other: Any?): Boolean {
        if (other !is SheetTodo) return false
        return other.id == this.id
    }

    fun switch() {
        id = if (id == -1) 0 else -1
    }
}

sealed class TodoViewModelNotification {
    class DisplaySaved(val message: String) : TodoViewModelNotification()
    object Neutral : TodoViewModelNotification()
}

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val notifyRepository: TodoNotifyRepository,
    private val todoRepository: TodoRepository,
    private val notifyManager: NotifyManager
) : ViewModel() {

    private val _todos = MutableLiveData<Todos>()
    val todos: LiveData<Todos>
        get() = _todos

    private val _viewModelNotification = MutableLiveData<TodoViewModelNotification>()
    val viewModelNotification: LiveData<TodoViewModelNotification>
        get() = _viewModelNotification

    private val _sheetTodo = MutableLiveData<SheetTodo>()
    val sheetTodo: LiveData<SheetTodo>
        get() = _sheetTodo

    /*
    Store todos in backing field and sort them appropriately
     */
    fun fetch() {
        viewModelScope.launch {
            _todos.value = Todos(
                todoRepository.todos()
                    .toMutableList()
                    .also { todos ->
                        todos.sortWith(compareByDescending<Todo> { !it.isCompleted }
                            .thenByDescending { it.title })
                    }
            )
        }
    }

    fun deleteTodo(todo: Todo) {
        val todos = _todos.value ?: return
        viewModelScope.launch {
            todoRepository.delete(todo.id)
            todos.todos.remove(todo)
            reload()
        }
    }

    fun deleteNotify(notifyId: Int) {
        viewModelScope.launch {
            notifyRepository.removeTodoNotify(notifyId)
        }
    }

    fun deleteCompleted() {
        val todos = _todos.value ?: return
        viewModelScope.launch {
            todoRepository.delete(
                todos.todos.filter { it.isCompleted }
            )
        }
        todos.todos.removeAll { it.isCompleted }
        reload()
    }

    fun sortByDate() = reload()

    fun sortAscending() {
        val todos = _todos.value ?: return
        todos.todos.sortWith(compareByDescending<Todo> { !it.isCompleted }.thenBy { it.title })
        todos.switch()
        _todos.value = todos
    }

    fun sortDescending() {
        val todos = _todos.value ?: return
        todos.todos.sortWith(compareByDescending<Todo> { !it.isCompleted }.thenByDescending { it.title })
        todos.switch()
        _todos.value = todos
    }

    fun reload() {
        val todos = _todos.value ?: return
        todos.todos.sortWith(compareByDescending<Todo> { !it.isCompleted }.thenByDescending { it.date })
        todos.switch()
        _todos.value = todos
    }

    fun setSavable(isSavable: Boolean) {
        val todoSheet = _sheetTodo.value ?: return
        todoSheet.canSave = isSavable && todoSheet.todo.title.isNotEmpty()
        todoSheet.switch()
        _sheetTodo.value = todoSheet
    }

    fun onTodoText(editable: Editable) {
        val todoSheet = _sheetTodo.value ?: return
        todoSheet.todo.title = editable.toString()
        todoSheet.canSave = editable.isNotEmpty()
        todoSheet.switch()
        _sheetTodo.value = todoSheet
    }

    fun getTodo(): Todo? = _sheetTodo.value?.todo

    fun createTodo() {
        _sheetTodo.value = SheetTodo.New(Todo(title = ""))
    }

    fun updateTodo(todo: Todo) {
        _sheetTodo.value = SheetTodo.Update(todo)
    }

    fun updateAdapterTodo(todo: Todo) {
        viewModelScope.launch {
            todoRepository.update(todo)
        }
    }

    fun <T> neutralize(item: T) {
        when (item) {
            is TodoViewModelNotification -> _viewModelNotification.value =
                TodoViewModelNotification.Neutral
            is SheetTodo -> _sheetTodo.value = SheetTodo.Neutral
        }
    }

    private suspend fun insertOrUpdateTodoNotify(todo: Todo, isUpdate: Boolean) {
            if (isUpdate) notifyManager.updateTodoNotify(
                TodoNotify(todo.id, todo.alertTime)
            )
            else notifyManager.addTodoNotify(
                TodoNotify(todo.id, todo.alertTime)
            )
    }

    fun saveTodo(shouldAlert: Boolean) {
        if (_sheetTodo.value == null || _sheetTodo.value!!.todo.title.isEmpty()) {
            _viewModelNotification.value = TodoViewModelNotification.DisplaySaved("Failed")
            return
        }
        //add todo to notification bus if necessary
        viewModelScope.launch {
            when (val sheetTodo = _sheetTodo.value!!) {
                is SheetTodo.New -> {
                    sheetTodo.todo.id = todoRepository.create(sheetTodo.todo)
                    _todos.value?.todos?.add(0, sheetTodo.todo)
                    if (shouldAlert) {
                        insertOrUpdateTodoNotify(sheetTodo.todo, false)
                    }
                    _viewModelNotification.value =
                        TodoViewModelNotification.DisplaySaved("To-do Saved!")
                    reload()
                    neutralize(SheetTodo::class)
                }
                is SheetTodo.Update -> {
                    todoRepository.update(sheetTodo.todo)
                    if (shouldAlert) {
                        insertOrUpdateTodoNotify(sheetTodo.todo, true)
                    }
                    _viewModelNotification.value =
                        TodoViewModelNotification.DisplaySaved("To-do Updated!")
                    reload()
                    neutralize(SheetTodo::class)
                }
                else -> {
                }
            }
        }
    }
}