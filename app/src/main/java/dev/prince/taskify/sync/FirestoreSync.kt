package dev.prince.taskify.sync

import android.content.Context
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import dev.prince.taskify.database.Task
import dev.prince.taskify.signin.GoogleAuthUiClient

class FirestoreSync(
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
        val tasksCollection = getTasksCollection() ?: return

        tasksCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val tasks = querySnapshot.documents.mapNotNull { it.toObject(Task::class.java) }
                onTasksFetched(tasks)
            }
            .addOnFailureListener { }
    }

    fun addTaskToFirestore(task: Task) {
        val tasksCollection = getTasksCollection()
        tasksCollection?.document(task.id.toString())?.set(task)
            ?.addOnSuccessListener { }
            ?.addOnFailureListener { }
    }

    fun updateTaskInFirestore(task: Task) {
        val tasksCollection = getTasksCollection()
        tasksCollection?.document(task.id.toString())?.set(task)
            ?.addOnSuccessListener { }
            ?.addOnFailureListener { }
    }

    fun deleteTaskFromFirestore(taskId: String) {
        val tasksCollection = getTasksCollection()
        tasksCollection?.document(taskId)?.delete()
            ?.addOnSuccessListener { }
            ?.addOnFailureListener { }
    }

}