package com.example.schedule.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.schedule.data.models.TodoItem

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo_items ORDER BY createdAt DESC")
    fun getAllTodos(): LiveData<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE isCompleted = 0 ORDER BY priority DESC, createdAt DESC")
    fun getActiveTodos(): LiveData<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE isCompleted = 1 ORDER BY updatedAt DESC")
    fun getCompletedTodos(): LiveData<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE category = :category ORDER BY createdAt DESC")
    fun getTodosByCategory(category: String): LiveData<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE id = :id")
    suspend fun getTodoById(id: String): TodoItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: TodoItem)

    @Update
    suspend fun updateTodo(todo: TodoItem)

    @Delete
    suspend fun deleteTodo(todo: TodoItem)

    @Query("DELETE FROM todo_items WHERE id = :id")
    suspend fun deleteTodoById(id: String)

    @Query("SELECT DISTINCT category FROM todo_items ORDER BY category ASC")
    fun getAllCategories(): LiveData<List<String>>
}