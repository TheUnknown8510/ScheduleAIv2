package com.example.schedule

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false
)

class HomeFragment : Fragment() {
    private lateinit var tasksContainer: LinearLayout
    private lateinit var addTaskButton: Button
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()
    private val tasks = mutableListOf<Task>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        sharedPreferences = requireContext().getSharedPreferences("tasks_prefs", Context.MODE_PRIVATE)
        
        tasksContainer = view.findViewById(R.id.tasks_container)
        addTaskButton = view.findViewById(R.id.add_task_button)
        
        addTaskButton.setOnClickListener {
            showAddTaskDialog()
        }
        
        loadTasks()
        displayTasks()
    }

    private fun showAddTaskDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_task, null)
        val titleInput = dialogView.findViewById<TextInputEditText>(R.id.task_title_input)
        val descriptionInput = dialogView.findViewById<TextInputEditText>(R.id.task_description_input)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add New Task")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val title = titleInput.text.toString().trim()
                val description = descriptionInput.text.toString().trim()
                if (title.isNotEmpty()) {
                    addTask(title, description)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addTask(title: String, description: String) {
        val task = Task(
            id = System.currentTimeMillis().toString(),
            title = title,
            description = description
        )
        tasks.add(task)
        saveTasks()
        displayTasks()
    }

    private fun removeTask(task: Task) {
        tasks.remove(task)
        saveTasks()
        displayTasks()
    }

    private fun saveTasks() {
        val json = gson.toJson(tasks)
        sharedPreferences.edit().putString("tasks", json).apply()
    }

    private fun loadTasks() {
        val json = sharedPreferences.getString("tasks", null)
        if (json != null) {
            val type = object : TypeToken<List<Task>>() {}.type
            val loadedTasks: List<Task> = gson.fromJson(json, type)
            tasks.clear()
            tasks.addAll(loadedTasks)
        }
    }

    private fun displayTasks() {
        tasksContainer.removeAllViews()
        
        if (tasks.isEmpty()) {
            val emptyView = TextView(requireContext()).apply {
                text = "No tasks yet. Add your first task!"
                textSize = 16f
                setPadding(16, 32, 16, 32)
                gravity = android.view.Gravity.CENTER
            }
            tasksContainer.addView(emptyView)
            return
        }

        for (task in tasks) {
            val taskView = LayoutInflater.from(requireContext()).inflate(R.layout.item_task, tasksContainer, false)
            
            val titleTextView = taskView.findViewById<TextView>(R.id.task_title)
            val descriptionTextView = taskView.findViewById<TextView>(R.id.task_description)
            val deleteButton = taskView.findViewById<Button>(R.id.delete_task_button)
            
            titleTextView.text = task.title
            descriptionTextView.text = task.description
            descriptionTextView.visibility = if (task.description.isNotEmpty()) View.VISIBLE else View.GONE
            
            deleteButton.setOnClickListener {
                removeTask(task)
            }
            
            tasksContainer.addView(taskView)
        }
    }
}