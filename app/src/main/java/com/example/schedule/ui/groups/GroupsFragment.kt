package com.example.schedule.ui.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.schedule.R

class GroupsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val textView = TextView(context)
        textView.text = "Groups Fragment"
        textView.textSize = 18f
        textView.setTextColor(resources.getColor(R.color.samsung_white, null))
        textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        return textView
    }
}