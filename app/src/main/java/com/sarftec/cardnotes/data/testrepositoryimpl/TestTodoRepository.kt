package com.sarftec.cardnotes.data.testrepositoryimpl

import com.sarftec.cardnotes.data.repository.TodoRepository
import com.sarftec.cardnotes.model.Todo
import javax.inject.Inject

class TestTodoRepository @Inject constructor() : TodoRepository {

    private val todos = mutableListOf(
        Todo(ID++, title = "Going to school"),
        Todo(ID++, title = "Farming for today"),
        Todo(ID++, title = "Working on the lawn"),
        Todo(ID++, title = "Studying for today"),
        Todo(ID++, title = "Looking for a job"),
        Todo(ID++, title = "Sitting in the house")
    )

    override suspend fun create(todo: Todo) : Int {
        todo.id = ID++
        todos.add(todo)
        return todo.id
    }

    override suspend fun delete(id: Int) {
        todos.firstOrNull { it.id == id }
            ?.let { todos.remove(it) }
    }

    override suspend fun delete(todos: List<Todo>) {
        this.todos.removeAll(todos)
    }

    override suspend fun update(todo: Todo) {
        todos.firstOrNull { it.id == todo.id }
            ?.let {
                it.title = todo.title
                it.isCompleted = todo.isCompleted
            }
    }

    override suspend fun todos(): List<Todo> {
        return todos
    }

    override suspend fun findTodo(id: Int): Todo? {
        return todos.firstOrNull { it.id == id }
    }

    companion object {
        var ID = 0
    }
}