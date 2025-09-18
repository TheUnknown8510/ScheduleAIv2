package com.example.schedule

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Friend(
    val id: String,
    val name: String,
    val email: String
)

data class Group(
    val id: String,
    val name: String,
    val description: String,
    val members: MutableList<String> = mutableListOf()
)

class GroupsFragment : Fragment() {
    private lateinit var friendsContainer: LinearLayout
    private lateinit var groupsContainer: LinearLayout
    private lateinit var addFriendButton: Button
    private lateinit var addGroupButton: Button
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()
    private val friends = mutableListOf<Friend>()
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
        
        sharedPreferences = requireContext().getSharedPreferences("groups_prefs", Context.MODE_PRIVATE)
        
        friendsContainer = view.findViewById(R.id.friends_container)
        groupsContainer = view.findViewById(R.id.groups_container)
        addFriendButton = view.findViewById(R.id.add_friend_button)
        addGroupButton = view.findViewById(R.id.add_group_button)
        
        addFriendButton.setOnClickListener {
            showAddFriendDialog()
        }
        
        addGroupButton.setOnClickListener {
            showAddGroupDialog()
        }
        
        loadData()
        displayFriends()
        displayGroups()
    }

    private fun showAddFriendDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_friend, null)
        val nameInput = dialogView.findViewById<TextInputEditText>(R.id.friend_name_input)
        val emailInput = dialogView.findViewById<TextInputEditText>(R.id.friend_email_input)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add New Friend")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = nameInput.text.toString().trim()
                val email = emailInput.text.toString().trim()
                if (name.isNotEmpty() && email.isNotEmpty()) {
                    addFriend(name, email)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAddGroupDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_group, null)
        val nameInput = dialogView.findViewById<TextInputEditText>(R.id.group_name_input)
        val descriptionInput = dialogView.findViewById<TextInputEditText>(R.id.group_description_input)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Create New Group")
            .setView(dialogView)
            .setPositiveButton("Create") { _, _ ->
                val name = nameInput.text.toString().trim()
                val description = descriptionInput.text.toString().trim()
                if (name.isNotEmpty()) {
                    addGroup(name, description)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addFriend(name: String, email: String) {
        val friend = Friend(
            id = System.currentTimeMillis().toString(),
            name = name,
            email = email
        )
        friends.add(friend)
        saveData()
        displayFriends()
    }

    private fun addGroup(name: String, description: String) {
        val group = Group(
            id = System.currentTimeMillis().toString(),
            name = name,
            description = description
        )
        groups.add(group)
        saveData()
        displayGroups()
    }

    private fun removeFriend(friend: Friend) {
        friends.remove(friend)
        saveData()
        displayFriends()
    }

    private fun removeGroup(group: Group) {
        groups.remove(group)
        saveData()
        displayGroups()
    }

    private fun saveData() {
        val friendsJson = gson.toJson(friends)
        val groupsJson = gson.toJson(groups)
        sharedPreferences.edit()
            .putString("friends", friendsJson)
            .putString("groups", groupsJson)
            .apply()
    }

    private fun loadData() {
        val friendsJson = sharedPreferences.getString("friends", null)
        val groupsJson = sharedPreferences.getString("groups", null)
        
        if (friendsJson != null) {
            val type = object : TypeToken<List<Friend>>() {}.type
            val loadedFriends: List<Friend> = gson.fromJson(friendsJson, type)
            friends.clear()
            friends.addAll(loadedFriends)
        }
        
        if (groupsJson != null) {
            val type = object : TypeToken<List<Group>>() {}.type
            val loadedGroups: List<Group> = gson.fromJson(groupsJson, type)
            groups.clear()
            groups.addAll(loadedGroups)
        }
    }

    private fun displayFriends() {
        friendsContainer.removeAllViews()
        
        if (friends.isEmpty()) {
            val emptyView = TextView(requireContext()).apply {
                text = "No friends added yet"
                textSize = 14f
                setPadding(16, 16, 16, 16)
                gravity = android.view.Gravity.CENTER
            }
            friendsContainer.addView(emptyView)
            return
        }

        for (friend in friends) {
            val friendView = LayoutInflater.from(requireContext()).inflate(R.layout.item_friend, friendsContainer, false)
            
            val nameTextView = friendView.findViewById<TextView>(R.id.friend_name)
            val emailTextView = friendView.findViewById<TextView>(R.id.friend_email)
            val deleteButton = friendView.findViewById<Button>(R.id.delete_friend_button)
            
            nameTextView.text = friend.name
            emailTextView.text = friend.email
            
            deleteButton.setOnClickListener {
                removeFriend(friend)
            }
            
            friendsContainer.addView(friendView)
        }
    }

    private fun displayGroups() {
        groupsContainer.removeAllViews()
        
        if (groups.isEmpty()) {
            val emptyView = TextView(requireContext()).apply {
                text = "No groups created yet"
                textSize = 14f
                setPadding(16, 16, 16, 16)
                gravity = android.view.Gravity.CENTER
            }
            groupsContainer.addView(emptyView)
            return
        }

        for (group in groups) {
            val groupView = LayoutInflater.from(requireContext()).inflate(R.layout.item_group, groupsContainer, false)
            
            val nameTextView = groupView.findViewById<TextView>(R.id.group_name)
            val descriptionTextView = groupView.findViewById<TextView>(R.id.group_description)
            val membersTextView = groupView.findViewById<TextView>(R.id.group_members)
            val deleteButton = groupView.findViewById<Button>(R.id.delete_group_button)
            
            nameTextView.text = group.name
            descriptionTextView.text = group.description
            descriptionTextView.visibility = if (group.description.isNotEmpty()) View.VISIBLE else View.GONE
            membersTextView.text = "${group.members.size} members"
            
            deleteButton.setOnClickListener {
                removeGroup(group)
            }
            
            groupsContainer.addView(groupView)
        }
    }
}