package dev.prince.taskify.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String = "",
    val description: String? = null,
    val isCompleted: Boolean = false,
    val isStarred: Boolean = false,
    val createdAt: Long? = System.currentTimeMillis()
) {
    constructor() : this(0, "", null, false, false, null)
}