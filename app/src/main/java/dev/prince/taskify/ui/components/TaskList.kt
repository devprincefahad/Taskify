package dev.prince.taskify.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.prince.taskify.database.Task

@Composable
fun TaskList(
    navigator: DestinationsNavigator,
    tasks: List<Task>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        items(tasks) { task ->
            TaskItem(
                navigator = navigator,
                task = task
            )
        }
    }
}