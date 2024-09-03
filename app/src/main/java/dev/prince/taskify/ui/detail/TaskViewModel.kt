package dev.prince.taskify.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.prince.taskify.database.Task
import dev.prince.taskify.database.TaskDao
import dev.prince.taskify.sync.FirestoreService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val firestoreService: FirestoreService
) : ViewModel() {

    fun getTaskById(id: Int): Flow<Task> {
        return taskDao.getTaskById(id)
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskDao.updateTask(task)
            firestoreService.updateTaskInFirestore(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskDao.deleteTask(task)
            firestoreService.deleteTaskFromFirestore(task.id)
        }
    }
}