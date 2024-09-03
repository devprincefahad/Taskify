package dev.prince.taskify.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.prince.taskify.database.Task
import dev.prince.taskify.database.TaskDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import dev.prince.taskify.sync.FirestoreService
import kotlinx.coroutines.flow.firstOrNull

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val firestoreService: FirestoreService
) : ViewModel() {

    val tasks: Flow<List<Task>> = taskDao.getAllTasks()
    val starredTasks: Flow<List<Task>> = taskDao.getStarredTasks()

    var isTaskDescVisible by mutableStateOf(false)

    init {
        syncDataIfNeeded()
    }

    private fun syncDataIfNeeded() {
        viewModelScope.launch {
            val localTasks = taskDao.getAllTasks().firstOrNull()
            if (localTasks.isNullOrEmpty()) {
                firestoreService.getAllTasksFromFirestore { tasksFromFirestore ->
                    viewModelScope.launch {
                        tasksFromFirestore.forEach { task ->
                            taskDao.insertTask(task)
                        }
                    }
                }
            }
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            val taskId = taskDao.insertTask(task)
            val updatedTask = task.copy(id = taskId.toInt())
            firestoreService.addTaskToFirestore(updatedTask)
        }
    }

    fun markTaskAsCompleted(task: Task) {
        viewModelScope.launch {
            val updatedTask = task.copy(isCompleted = true)
            taskDao.updateTask(updatedTask)
            firestoreService.updateTaskInFirestore(updatedTask)
        }
    }

    fun markTaskAsUncompleted(task: Task) {
        viewModelScope.launch {
            val updatedTask = task.copy(isCompleted = false)
            taskDao.updateTask(updatedTask)
            firestoreService.updateTaskInFirestore(updatedTask)
        }
    }

    fun starTask(task: Task) {
        viewModelScope.launch {
            val updatedTask = task.copy(isStarred = true)
            taskDao.updateTask(updatedTask)
            firestoreService.updateTaskInFirestore(updatedTask)
        }
    }

    fun unStarTask(task: Task) {
        viewModelScope.launch {
            val updatedTask = task.copy(isStarred = false)
            taskDao.updateTask(updatedTask)
            firestoreService.updateTaskInFirestore(updatedTask)
        }
    }
}