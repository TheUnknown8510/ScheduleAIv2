package com.example.schedule.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.schedule.R
import com.example.schedule.data.models.Priority
import com.example.schedule.data.models.TodoItem
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        
        setupRecyclerView(view)
        setupFab(view)
        observeViewModel()
    }
    
    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.recycler_todos)
        emptyStateView = view.findViewById(R.id.empty_state)
        
        todoAdapter = TodoAdapter(
            onTodoClick = { todo ->
                // TODO: Open edit todo dialog/activity
                Snackbar.make(view, "Edit ${todo.title}", Snackbar.LENGTH_SHORT).show()
            },
            onToggleComplete = { todo ->
                homeViewModel.toggleTodoComplete(todo)
            }
        )
        
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = todoAdapter
        }
    }
    
    private fun setupFab(view: View) {
        val fab = view.findViewById<FloatingActionButton>(R.id.fab_add_todo)
        fab.setOnClickListener {
            // Create a sample todo for demonstration
            val sampleTodo = TodoItem(
                title = "Sample Task #${System.currentTimeMillis() % 1000}",
                description = "This is a sample todo item created from the FAB",
                priority = Priority.values().random(),
                category = listOf("Work", "Personal", "Shopping", "Health").random()
            )
            homeViewModel.insertTodo(sampleTodo)
            
            Snackbar.make(view, "Todo added!", Snackbar.LENGTH_SHORT).show()
        }
    }
    
    private fun observeViewModel() {
        homeViewModel.activeTodos.observe(viewLifecycleOwner) { todos ->
            todoAdapter.submitList(todos)
            
            // Show/hide empty state
            if (todos.isEmpty()) {
                recyclerView.visibility = View.GONE
                emptyStateView.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                emptyStateView.visibility = View.GONE
            }
        }
    }
}