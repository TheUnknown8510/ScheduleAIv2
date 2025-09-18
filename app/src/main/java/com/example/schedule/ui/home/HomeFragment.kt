package com.example.schedule.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.schedule.R
import com.example.schedule.data.models.TodoItem
import com.example.schedule.ui.create.CreateTodoActivity
import com.example.schedule.utils.TodoExporter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateView: View
    private var currentTodos: List<TodoItem> = emptyList()

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
        
        setupToolbar(view)
        setupRecyclerView(view)
        setupFab(view)
        observeViewModel()
    }
    
    private fun setupToolbar(view: View) {
        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_share -> {
                    shareTodos()
                    true
                }
                else -> false
            }
        }
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
            // Open CreateTodoActivity
            val intent = Intent(context, CreateTodoActivity::class.java)
            startActivity(intent)
        }
    }
    
    private fun observeViewModel() {
        homeViewModel.allTodos.observe(viewLifecycleOwner) { todos ->
            currentTodos = todos
            val activeTodos = todos.filter { !it.isCompleted }
            todoAdapter.submitList(activeTodos)
            
            // Show/hide empty state
            if (activeTodos.isEmpty()) {
                recyclerView.visibility = View.GONE
                emptyStateView.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                emptyStateView.visibility = View.GONE
            }
        }
    }
    
    private fun shareTodos() {
        if (currentTodos.isEmpty()) {
            view?.let {
                Snackbar.make(it, "No todos to share", Snackbar.LENGTH_SHORT).show()
            }
            return
        }
        
        context?.let { ctx ->
            TodoExporter.shareTodos(ctx, currentTodos, TodoExporter.ExportFormat.TEXT)
        }
    }
}