package com.example.schedule.data.database

import androidx.room.TypeConverter
import com.example.schedule.data.models.Priority

class Converters {
    @TypeConverter
    fun fromPriority(priority: Priority): String {
        return priority.name
    }

    @TypeConverter
    fun toPriority(priority: String): Priority {
        return Priority.valueOf(priority)
    }
}