package com.example.schedule.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.schedule.data.database.TodoDatabase
import com.example.schedule.data.models.TodoItem
import com.example.schedule.data.repository.TodoRepository
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: TodoRepository
    val allTodos: LiveData<List<TodoItem>>
    val activeTodos: LiveData<List<TodoItem>>
    
    init {
        val todoDao = TodoDatabase.getDatabase(application).todoDao()
        repository = TodoRepository(todoDao)
        allTodos = repository.getAllTodos()
        activeTodos = repository.getActiveTodos()
    }
    
    fun insertTodo(todo: TodoItem) = viewModelScope.launch {
        repository.insertTodo(todo)
    }
    
    fun updateTodo(todo: TodoItem) = viewModelScope.launch {
        repository.updateTodo(todo)
    }
    
    fun deleteTodo(todo: TodoItem) = viewModelScope.launch {
        repository.deleteTodo(todo)
    }
    
    fun toggleTodoComplete(todo: TodoItem) = viewModelScope.launch {
        val updatedTodo = todo.copy(
            isCompleted = !todo.isCompleted,
            updatedAt = System.currentTimeMillis()
        )
        repository.updateTodo(updatedTodo)
    }
}