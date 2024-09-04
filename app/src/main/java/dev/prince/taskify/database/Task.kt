package dev.prince.taskify.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String = "",
    val description: String? = null,
    @get:PropertyName("isCompleted") val isCompleted: Boolean = false,
    @get:PropertyName("isStarred") val isStarred: Boolean = false,
    val createdAt: Long? = System.currentTimeMillis()
)