package com.example.schedule.data.repository

import androidx.lifecycle.LiveData
import com.example.schedule.data.database.TodoDao
import com.example.schedule.data.models.TodoItem

class TodoRepository(private val todoDao: TodoDao) {
    
    fun getAllTodos(): LiveData<List<TodoItem>> = todoDao.getAllTodos()
    
    fun getActiveTodos(): LiveData<List<TodoItem>> = todoDao.getActiveTodos()
    
    fun getCompletedTodos(): LiveData<List<TodoItem>> = todoDao.getCompletedTodos()
    
    fun getTodosByCategory(category: String): LiveData<List<TodoItem>> = todoDao.getTodosByCategory(category)
    
    suspend fun getTodoById(id: String): TodoItem? = todoDao.getTodoById(id)
    
    suspend fun insertTodo(todo: TodoItem) = todoDao.insertTodo(todo)
    
    suspend fun updateTodo(todo: TodoItem) = todoDao.updateTodo(todo)
    
    suspend fun deleteTodo(todo: TodoItem) = todoDao.deleteTodo(todo)
    
    suspend fun deleteTodoById(id: String) = todoDao.deleteTodoById(id)
    
    fun getAllCategories(): LiveData<List<String>> = todoDao.getAllCategories()
}