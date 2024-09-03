package dev.prince.taskify.sync

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import dev.prince.taskify.database.Task

class FirestoreService(private val firestore: FirebaseFirestore) {

    private val tasksCollection = firestore.collection("tasks")

    fun getAllTasksFromFirestore(onTasksFetched: (List<Task>) -> Unit) {
        firestore.collection("tasks")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val tasks = querySnapshot.toObjects(Task::class.java)
                onTasksFetched(tasks)
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreService", "Error fetching tasks", exception)
            }
    }

    fun addTaskToFirestore(task: Task) {
        tasksCollection.document(task.id.toString()).set(task)
            .addOnSuccessListener { /* Handle success */ }
            .addOnFailureListener { /* Handle failure */ }
    }

    fun updateTaskInFirestore(task: Task) {
        tasksCollection.document(task.id.toString()).set(task)
            .addOnSuccessListener { /* Handle success */ }
            .addOnFailureListener { /* Handle failure */ }
    }

    fun deleteTaskFromFirestore(taskId: Int) {
        tasksCollection.document(taskId.toString()).delete()
            .addOnSuccessListener { /* Handle success */ }
            .addOnFailureListener { /* Handle failure */ }
    }
}
