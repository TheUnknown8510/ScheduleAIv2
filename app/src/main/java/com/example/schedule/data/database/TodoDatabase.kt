package com.example.schedule.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.schedule.data.models.TodoItem

@Database(
    entities = [TodoItem::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TodoDatabase : RoomDatabase() {
    
    abstract fun todoDao(): TodoDao
    
    companion object {
        @Volatile
        private var INSTANCE: TodoDatabase? = null
        
        fun getDatabase(context: Context): TodoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TodoDatabase::class.java,
                    "todo_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}