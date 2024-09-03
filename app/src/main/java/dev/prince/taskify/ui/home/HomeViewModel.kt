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

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskDao: TaskDao
) : ViewModel() {

    val tasks: Flow<List<Task>> = taskDao.getAllTasks()
    val starredTasks: Flow<List<Task>> = taskDao.getStarredTasks()

    var isTaskDescVisible by mutableStateOf(false)

    fun addTask(task: Task) {
        viewModelScope.launch {
            taskDao.insertTask(task)
        }
    }

    fun markTaskAsCompleted(task: Task) {
        viewModelScope.launch {
            taskDao.updateTask(task.copy(isCompleted = true))
        }
    }

    fun markTaskAsUncompleted(task: Task) {
        viewModelScope.launch {
            taskDao.updateTask(task.copy(isCompleted = false))
        }
    }

    fun starTask(task: Task) {
        viewModelScope.launch {
            taskDao.updateTask(task.copy(isStarred = true))
        }
    }

    fun unStarTask(task: Task) {
        viewModelScope.launch {
            taskDao.updateTask(task.copy(isStarred = false))
        }
    }
}