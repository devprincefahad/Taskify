package dev.prince.taskify.ui.home

import android.util.Log
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
import dev.prince.taskify.sync.FirestoreSync
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val firestoreSync: FirestoreSync
) : ViewModel() {

    val tasks: Flow<List<Task>> = taskDao.getAllTasks()
    val starredTasks: Flow<List<Task>> = taskDao.getStarredTasks()

    var isTaskDescVisible by mutableStateOf(false)

    private val _messages = MutableSharedFlow<String>()
    val messages: SharedFlow<String> get() = _messages

    init {
        syncDataIfNeeded()
    }

    private fun syncDataIfNeeded() {
        viewModelScope.launch {
            val localTasks = taskDao.getAllTasks().firstOrNull()
            if (localTasks.isNullOrEmpty()) {
                firestoreSync.getAllTasksFromFirestore { tasksFromFirestore ->
                    viewModelScope.launch {
                        taskDao.insertTasks(tasksFromFirestore)
                        Log.d("HomeViewModel", "Tasks inserted into local database")
                    }
                }
            }
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            val taskId = taskDao.insertTask(task).toInt()
            val updatedTask = task.copy(id = taskId)
            firestoreSync.addTaskToFirestore(updatedTask)
            _messages.emit("Task Added!")
        }
    }

    fun markTaskAsCompleted(task: Task) {
        viewModelScope.launch {
            val updatedTask = task.copy(isCompleted = true)
            taskDao.updateTask(updatedTask)
            firestoreSync.updateTaskInFirestore(updatedTask)
        }
    }

    fun markTaskAsUncompleted(task: Task) {
        viewModelScope.launch {
            val updatedTask = task.copy(isCompleted = false)
            taskDao.updateTask(updatedTask)
            firestoreSync.updateTaskInFirestore(updatedTask)
        }
    }

    fun starTask(task: Task) {
        viewModelScope.launch {
            val updatedTask = task.copy(isStarred = true)
            taskDao.updateTask(updatedTask)
            firestoreSync.updateTaskInFirestore(updatedTask)
        }
    }

    fun unStarTask(task: Task) {
        viewModelScope.launch {
            val updatedTask = task.copy(isStarred = false)
            taskDao.updateTask(updatedTask)
            firestoreSync.updateTaskInFirestore(updatedTask)
        }
    }
}