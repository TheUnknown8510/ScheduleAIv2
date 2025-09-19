package com.example.schedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GroupsAdapter(
    private val groups: List<Group>,
    private val onDeleteClick: (Group) -> Unit
) : RecyclerView.Adapter<GroupsAdapter.GroupViewHolder>() {

    class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.tv_group_name)
        val descriptionTextView: TextView = itemView.findViewById(R.id.tv_group_description)
        val deleteButton: ImageButton = itemView.findViewById(R.id.btn_delete_group)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_group, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groups[position]
        holder.nameTextView.text = group.name
        holder.descriptionTextView.text = group.description
        holder.descriptionTextView.visibility = if (group.description.isNotEmpty()) View.VISIBLE else View.GONE
        
        holder.deleteButton.setOnClickListener {
            onDeleteClick(group)
        }
    }

    override fun getItemCount(): Int = groups.size
}