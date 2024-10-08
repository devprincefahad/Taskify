package dev.prince.taskify.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class TaskifyDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
