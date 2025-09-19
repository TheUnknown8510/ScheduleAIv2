package com.example.schedule

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class GroupsFragment : Fragment() {
    
    private lateinit var groupsRecyclerView: RecyclerView
    private lateinit var groupsAdapter: GroupsAdapter
    private lateinit var fabAddGroup: FloatingActionButton
    private lateinit var sharedPreferences: SharedPreferences
    private val groups = mutableListOf<Group>()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_groups, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        sharedPreferences = requireContext().getSharedPreferences("schedule_prefs", Context.MODE_PRIVATE)
        
        setupRecyclerView(view)
        setupFab(view)
        loadGroups()
    }
    
    private fun setupRecyclerView(view: View) {
        groupsRecyclerView = view.findViewById(R.id.rv_groups)
        groupsAdapter = GroupsAdapter(groups) { group ->
            deleteGroup(group)
        }
        groupsRecyclerView.layoutManager = LinearLayoutManager(context)
        groupsRecyclerView.adapter = groupsAdapter
    }
    
    private fun setupFab(view: View) {
        fabAddGroup = view.findViewById(R.id.fab_add_group)
        fabAddGroup.setOnClickListener {
            showAddGroupDialog()
        }
    }
    
    private fun showAddGroupDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_group, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.et_group_name)
        val descriptionEditText = dialogView.findViewById<EditText>(R.id.et_group_description)
        
        AlertDialog.Builder(requireContext())
            .setTitle("Add New Group/Friend")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = nameEditText.text.toString().trim()
                val description = descriptionEditText.text.toString().trim()
                if (name.isNotEmpty()) {
                    addGroup(Group(name, description))
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun addGroup(group: Group) {
        groups.add(group)
        groupsAdapter.notifyItemInserted(groups.size - 1)
        saveGroups()
    }
    
    private fun deleteGroup(group: Group) {
        val position = groups.indexOf(group)
        if (position != -1) {
            groups.removeAt(position)
            groupsAdapter.notifyItemRemoved(position)
            saveGroups()
        }
    }
    
    private fun saveGroups() {
        val groupsJson = groups.joinToString("|") { "${it.name}~${it.description}" }
        sharedPreferences.edit().putString("groups", groupsJson).apply()
    }
    
    private fun loadGroups() {
        val groupsJson = sharedPreferences.getString("groups", "") ?: ""
        if (groupsJson.isNotEmpty()) {
            groups.clear()
            groupsJson.split("|").forEach { groupString ->
                val parts = groupString.split("~")
                if (parts.size == 2) {
                    groups.add(Group(parts[0], parts[1]))
                }
            }
            groupsAdapter.notifyDataSetChanged()
        }
    }
}

data class Group(
    val name: String,
    val description: String
)