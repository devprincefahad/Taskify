package dev.prince.taskify.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.prince.taskify.database.Task
import dev.prince.taskify.database.TaskDao
import dev.prince.taskify.sync.FirestoreService
import dev.prince.taskify.util.oneShotFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val firestoreService: FirestoreService
) : ViewModel() {

    val messages = oneShotFlow<String>()

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
            firestoreService.deleteTaskFromFirestore(task.id.toString())
            messages.tryEmit("Task Deleted!")
        }
    }

    fun showSnackBarMessage(msg: String) {
        messages.tryEmit(msg)
    }

}