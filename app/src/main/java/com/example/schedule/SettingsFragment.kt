package com.example.schedule

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsFragment : Fragment() {
    private lateinit var themeSwitch: Switch
    private lateinit var clearDataButton: Button
    private lateinit var aboutButton: Button
    private lateinit var versionTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        
        themeSwitch = view.findViewById(R.id.theme_switch)
        clearDataButton = view.findViewById(R.id.clear_data_button)
        aboutButton = view.findViewById(R.id.about_button)
        versionTextView = view.findViewById(R.id.version_text)
        
        setupThemeSwitch()
        setupButtons()
        setupVersionInfo()
    }

    private fun setupThemeSwitch() {
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        themeSwitch.isChecked = isDarkMode
        
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply()
            
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun setupButtons() {
        clearDataButton.setOnClickListener {
            showClearDataDialog()
        }
        
        aboutButton.setOnClickListener {
            showAboutDialog()
        }
    }

    private fun setupVersionInfo() {
        try {
            val packageInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            versionTextView.text = "Version ${packageInfo.versionName}"
        } catch (e: Exception) {
            versionTextView.text = "Version 1.0"
        }
    }

    private fun showClearDataDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Clear All Data")
            .setMessage("This will delete all your tasks, friends, and groups. This action cannot be undone.")
            .setPositiveButton("Clear") { _, _ ->
                clearAllData()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAboutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("About ScheduleAI v2")
            .setMessage("ScheduleAI v2 is a powerful task management and scheduling application that helps you organize your daily activities, collaborate with friends, and stay productive.\n\n" +
                    "Features:\n" +
                    "• Task management with descriptions\n" +
                    "• Friend and group management\n" +
                    "• Dark/Light theme support\n" +
                    "• Data persistence\n\n" +
                    "Developed with ❤️ for better productivity.")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun clearAllData() {
        // Clear tasks
        val tasksPrefs = requireContext().getSharedPreferences("tasks_prefs", Context.MODE_PRIVATE)
        tasksPrefs.edit().clear().apply()
        
        // Clear groups
        val groupsPrefs = requireContext().getSharedPreferences("groups_prefs", Context.MODE_PRIVATE)
        groupsPrefs.edit().clear().apply()
        
        // Show confirmation
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Data Cleared")
            .setMessage("All data has been successfully cleared.")
            .setPositiveButton("OK", null)
            .show()
    }
}