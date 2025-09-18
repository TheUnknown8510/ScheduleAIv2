package com.example.schedule.utils

import android.content.Context
import android.content.Intent
import com.example.schedule.data.models.TodoItem
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

object TodoExporter {
    
    fun exportTodosAsJson(todos: List<TodoItem>): String {
        val jsonArray = JSONArray()
        
        for (todo in todos) {
            val jsonObject = JSONObject().apply {
                put("id", todo.id)
                put("title", todo.title)
                put("description", todo.description)
                put("isCompleted", todo.isCompleted)
                put("priority", todo.priority.name)
                put("category", todo.category)
                put("createdAt", todo.createdAt)
                put("updatedAt", todo.updatedAt)
            }
            jsonArray.put(jsonObject)
        }
        
        return JSONObject().apply {
            put("todos", jsonArray)
            put("exportedAt", System.currentTimeMillis())
            put("appVersion", "2.0")
        }.toString(2)
    }
    
    fun exportTodosAsText(todos: List<TodoItem>): String {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val stringBuilder = StringBuilder()
        
        stringBuilder.append("📋 Todo List Export\n")
        stringBuilder.append("Generated on: ${dateFormat.format(Date())}\n\n")
        
        val activeTodos = todos.filter { !it.isCompleted }
        val completedTodos = todos.filter { it.isCompleted }
        
        if (activeTodos.isNotEmpty()) {
            stringBuilder.append("✅ ACTIVE TASKS (${activeTodos.size})\n")
            stringBuilder.append("━━━━━━━━━━━━━━━━━━━━━━━━\n")
            
            for (todo in activeTodos) {
                stringBuilder.append("• ${todo.title}\n")
                if (todo.description.isNotEmpty()) {
                    stringBuilder.append("  ${todo.description}\n")
                }
                stringBuilder.append("  Priority: ${todo.priority.displayName} | Category: ${todo.category}\n")
                stringBuilder.append("  Created: ${dateFormat.format(Date(todo.createdAt))}\n\n")
            }
        }
        
        if (completedTodos.isNotEmpty()) {
            stringBuilder.append("✓ COMPLETED TASKS (${completedTodos.size})\n")
            stringBuilder.append("━━━━━━━━━━━━━━━━━━━━━━━━\n")
            
            for (todo in completedTodos) {
                stringBuilder.append("✓ ${todo.title}\n")
                if (todo.description.isNotEmpty()) {
                    stringBuilder.append("  ${todo.description}\n")
                }
                stringBuilder.append("  Category: ${todo.category}\n")
                stringBuilder.append("  Completed: ${dateFormat.format(Date(todo.updatedAt))}\n\n")
            }
        }
        
        stringBuilder.append("─────────────────────────\n")
        stringBuilder.append("Exported from ToDo Hub v2.0")
        
        return stringBuilder.toString()
    }
    
    fun shareTodos(context: Context, todos: List<TodoItem>, format: ExportFormat = ExportFormat.TEXT) {
        val content = when (format) {
            ExportFormat.JSON -> exportTodosAsJson(todos)
            ExportFormat.TEXT -> exportTodosAsText(todos)
        }
        
        val mimeType = when (format) {
            ExportFormat.JSON -> "application/json"
            ExportFormat.TEXT -> "text/plain"
        }
        
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = mimeType
            putExtra(Intent.EXTRA_TEXT, content)
            putExtra(Intent.EXTRA_SUBJECT, "My Todo List - ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())}")
        }
        
        context.startActivity(Intent.createChooser(shareIntent, "Share Todo List"))
    }
    
    enum class ExportFormat {
        JSON, TEXT
    }
}