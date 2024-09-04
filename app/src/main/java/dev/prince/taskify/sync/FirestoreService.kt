package dev.prince.taskify.sync

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import dev.prince.taskify.database.Task
import dev.prince.taskify.signin.GoogleAuthUiClient

class FirestoreService(
    private val firestore: FirebaseFirestore,
    private val context: Context
) {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = context,
            oneTapClient = Identity.getSignInClient(context)
        )
    }

    private val user get() = googleAuthUiClient.getSignedInUser()

    private fun getTasksCollection(): CollectionReference? {
        return user?.userId?.let { userId ->
            firestore.collection("users").document(userId).collection("tasks")
        }
    }

    fun getAllTasksFromFirestore(onTasksFetched: (List<Task>) -> Unit) {
        val tasksCollection = getTasksCollection()
        if (tasksCollection == null) {
            Log.e("FirestoreService", "User is not authenticated.")
            return
        }
        Log.d("FirestoreService", "Fetching tasks from Firestore...")
        tasksCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val tasks = querySnapshot.documents.mapNotNull { it.toObject(Task::class.java) }
                Log.d("FirestoreService", "Tasks fetched: ${tasks.size}")
                onTasksFetched(tasks)
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreService", "Error fetching tasks", exception)
            }
    }

    fun addTaskToFirestore(task: Task) {
        val tasksCollection = getTasksCollection()
        tasksCollection?.document(task.id.toString())?.set(task)
            ?.addOnSuccessListener { Log.d("FirestoreService", "Task added") }
            ?.addOnFailureListener { exception ->
                Log.e("FirestoreService", "Error adding task", exception)
            }
    }

    fun updateTaskInFirestore(task: Task) {
        val tasksCollection = getTasksCollection()
        tasksCollection?.document(task.id.toString())?.set(task)
            ?.addOnSuccessListener { Log.d("FirestoreService", "Task updated") }
            ?.addOnFailureListener { exception ->
                Log.e("FirestoreService", "Error updating task", exception)
            }
    }

    fun deleteTaskFromFirestore(taskId: String) {
        val tasksCollection = getTasksCollection()
        tasksCollection?.document(taskId)?.delete()
            ?.addOnSuccessListener { Log.d("FirestoreService", "Task deleted") }
            ?.addOnFailureListener { exception ->
                Log.e("FirestoreService", "Error deleting task", exception)
            }
    }

}