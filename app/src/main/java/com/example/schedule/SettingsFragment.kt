package com.example.schedule

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment() {
    
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var themeSwitch: Switch
    private lateinit var clearDataButton: TextView
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        sharedPreferences = requireContext().getSharedPreferences("schedule_prefs", Context.MODE_PRIVATE)
        
        setupViews(view)
        loadSettings()
    }
    
    private fun setupViews(view: View) {
        themeSwitch = view.findViewById(R.id.switch_theme)
        clearDataButton = view.findViewById(R.id.btn_clear_data)
        
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            setTheme(isChecked)
        }
        
        clearDataButton.setOnClickListener {
            clearAllData()
        }
    }
    
    private fun setTheme(isDarkTheme: Boolean) {
        val nightMode = if (isDarkTheme) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        
        sharedPreferences.edit().putBoolean("dark_theme", isDarkTheme).apply()
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }
    
    private fun clearAllData() {
        sharedPreferences.edit().clear().apply()
        // Refresh the fragment to show empty lists
        requireActivity().recreate()
    }
    
    private fun loadSettings() {
        val isDarkTheme = sharedPreferences.getBoolean("dark_theme", false)
        themeSwitch.isChecked = isDarkTheme
    }
}