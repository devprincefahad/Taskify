package dev.prince.taskify.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.prince.taskify.database.Task

@Composable
fun TaskList(
    navigator: DestinationsNavigator,
    tasks: List<Task>,
    bottomPadding: Dp
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        items(
            tasks,
            key = { it.id }
        ) { task ->
            TaskItem(
                navigator = navigator,
                task = task
            )
        }
        item {
            Spacer(modifier = Modifier.height(bottomPadding))
        }
    }
}