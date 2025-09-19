package com.example.schedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TasksAdapter(
    private val tasks: List<Task>,
    private val onDeleteClick: (Task) -> Unit
) : RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.tv_task_title)
        val descriptionTextView: TextView = itemView.findViewById(R.id.tv_task_description)
        val deleteButton: ImageButton = itemView.findViewById(R.id.btn_delete_task)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.titleTextView.text = task.title
        holder.descriptionTextView.text = task.description
        holder.descriptionTextView.visibility = if (task.description.isNotEmpty()) View.VISIBLE else View.GONE
        
        holder.deleteButton.setOnClickListener {
            onDeleteClick(task)
        }
    }

    override fun getItemCount(): Int = tasks.size
}