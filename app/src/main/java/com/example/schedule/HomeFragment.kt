package com.example.schedule

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {
    
    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var tasksAdapter: TasksAdapter
    private lateinit var fabAddTask: FloatingActionButton
    private lateinit var sharedPreferences: SharedPreferences
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
        
        sharedPreferences = requireContext().getSharedPreferences("schedule_prefs", Context.MODE_PRIVATE)
        
        setupRecyclerView(view)
        setupFab(view)
        loadTasks()
    }
    
    private fun setupRecyclerView(view: View) {
        tasksRecyclerView = view.findViewById(R.id.rv_tasks)
        tasksAdapter = TasksAdapter(tasks) { task ->
            deleteTask(task)
        }
        tasksRecyclerView.layoutManager = LinearLayoutManager(context)
        tasksRecyclerView.adapter = tasksAdapter
    }
    
    private fun setupFab(view: View) {
        fabAddTask = view.findViewById(R.id.fab_add_task)
        fabAddTask.setOnClickListener {
            showAddTaskDialog()
        }
    }
    
    private fun showAddTaskDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_task, null)
        val titleEditText = dialogView.findViewById<EditText>(R.id.et_task_title)
        val descriptionEditText = dialogView.findViewById<EditText>(R.id.et_task_description)
        
        AlertDialog.Builder(requireContext())
            .setTitle("Add New Task")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val title = titleEditText.text.toString().trim()
                val description = descriptionEditText.text.toString().trim()
                if (title.isNotEmpty()) {
                    addTask(Task(title, description))
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun addTask(task: Task) {
        tasks.add(task)
        tasksAdapter.notifyItemInserted(tasks.size - 1)
        saveTasks()
    }
    
    private fun deleteTask(task: Task) {
        val position = tasks.indexOf(task)
        if (position != -1) {
            tasks.removeAt(position)
            tasksAdapter.notifyItemRemoved(position)
            saveTasks()
        }
    }
    
    private fun saveTasks() {
        val tasksJson = tasks.joinToString("|") { "${it.title}~${it.description}" }
        sharedPreferences.edit().putString("tasks", tasksJson).apply()
    }
    
    private fun loadTasks() {
        val tasksJson = sharedPreferences.getString("tasks", "") ?: ""
        if (tasksJson.isNotEmpty()) {
            tasks.clear()
            tasksJson.split("|").forEach { taskString ->
                val parts = taskString.split("~")
                if (parts.size == 2) {
                    tasks.add(Task(parts[0], parts[1]))
                }
            }
            tasksAdapter.notifyDataSetChanged()
        }
    }
}

data class Task(
    val title: String,
    val description: String
)