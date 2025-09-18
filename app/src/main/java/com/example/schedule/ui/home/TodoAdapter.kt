package com.example.schedule.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.schedule.R
import com.example.schedule.data.models.TodoItem
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.*

class TodoAdapter(
    private val onTodoClick: (TodoItem) -> Unit,
    private val onToggleComplete: (TodoItem) -> Unit
) : ListAdapter<TodoItem, TodoAdapter.TodoViewHolder>(TodoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_todo, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: MaterialCardView = itemView.findViewById(R.id.card_todo)
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkbox_complete)
        private val titleText: TextView = itemView.findViewById(R.id.text_title)
        private val descriptionText: TextView = itemView.findViewById(R.id.text_description)
        private val categoryText: TextView = itemView.findViewById(R.id.text_category)
        private val dateText: TextView = itemView.findViewById(R.id.text_date)
        private val priorityIndicator: View = itemView.findViewById(R.id.priority_indicator)

        fun bind(todo: TodoItem) {
            titleText.text = todo.title
            descriptionText.text = todo.description
            categoryText.text = todo.category
            checkBox.isChecked = todo.isCompleted
            
            // Format date
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            dateText.text = dateFormat.format(Date(todo.createdAt))
            
            // Set priority indicator color
            val colorResId = todo.priority.colorResId
            priorityIndicator.setBackgroundColor(
                ContextCompat.getColor(itemView.context, colorResId)
            )
            
            // Handle completed state
            val alpha = if (todo.isCompleted) 0.6f else 1.0f
            itemView.alpha = alpha
            
            // Show/hide description
            descriptionText.visibility = if (todo.description.isNotEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
            
            // Click listeners
            cardView.setOnClickListener { onTodoClick(todo) }
            checkBox.setOnClickListener { onToggleComplete(todo) }
        }
    }
}

class TodoDiffCallback : DiffUtil.ItemCallback<TodoItem>() {
    override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
        return oldItem == newItem
    }
}