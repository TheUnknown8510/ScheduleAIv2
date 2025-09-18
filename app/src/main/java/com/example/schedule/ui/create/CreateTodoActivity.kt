package com.example.schedule.ui.create

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.schedule.R
import com.example.schedule.data.models.Priority
import com.example.schedule.data.models.TodoItem
import com.example.schedule.ui.home.HomeViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText

class CreateTodoActivity : AppCompatActivity() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var titleInput: TextInputEditText
    private lateinit var descriptionInput: TextInputEditText
    private lateinit var categoryInput: TextInputEditText
    private lateinit var prioritySpinner: MaterialAutoCompleteTextView
    private lateinit var saveButton: MaterialButton
    private lateinit var cancelButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_todo)
        
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        
        initViews()
        setupPrioritySpinner()
        setupClickListeners()
    }
    
    private fun initViews() {
        titleInput = findViewById(R.id.input_title)
        descriptionInput = findViewById(R.id.input_description)
        categoryInput = findViewById(R.id.input_category)
        prioritySpinner = findViewById(R.id.spinner_priority)
        saveButton = findViewById(R.id.btn_save)
        cancelButton = findViewById(R.id.btn_cancel)
    }
    
    private fun setupPrioritySpinner() {
        val priorities = Priority.values().map { it.displayName }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, priorities)
        prioritySpinner.setAdapter(adapter)
        prioritySpinner.setText(Priority.MEDIUM.displayName, false)
    }
    
    private fun setupClickListeners() {
        saveButton.setOnClickListener {
            saveTodo()
        }
        
        cancelButton.setOnClickListener {
            finish()
        }
    }
    
    private fun saveTodo() {
        val title = titleInput.text?.toString()?.trim()
        val description = descriptionInput.text?.toString()?.trim() ?: ""
        val category = categoryInput.text?.toString()?.trim() ?: "General"
        val priorityText = prioritySpinner.text.toString()
        
        if (title.isNullOrEmpty()) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show()
            return
        }
        
        val priority = Priority.values().find { it.displayName == priorityText } ?: Priority.MEDIUM
        
        val todo = TodoItem(
            title = title,
            description = description,
            category = category,
            priority = priority
        )
        
        homeViewModel.insertTodo(todo)
        Toast.makeText(this, "Todo created successfully!", Toast.LENGTH_SHORT).show()
        finish()
    }
}