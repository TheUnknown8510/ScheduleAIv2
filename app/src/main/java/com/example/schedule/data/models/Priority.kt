package com.example.schedule.data.models

enum class Priority(val displayName: String, val colorResId: Int) {
    HIGH("High", android.R.color.holo_red_light),
    MEDIUM("Medium", android.R.color.holo_orange_light),
    LOW("Low", android.R.color.holo_green_light)
}