package dev.prince.taskify.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.prince.taskify.R
import dev.prince.taskify.database.Task
import dev.prince.taskify.ui.destinations.TaskDetailScreenDestination
import dev.prince.taskify.ui.home.HomeViewModel
import dev.prince.taskify.ui.theme.poppinsFamily

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyItemScope.TaskItem(
    navigator: DestinationsNavigator,
    task: Task,
    viewModel: HomeViewModel = hiltViewModel()
) {
    Row(
        modifier = Modifier
            .animateItemPlacement()
            .fillMaxWidth()
            .clickable {
                navigator.navigate(TaskDetailScreenDestination(task.id))
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { isChecked ->
                if (isChecked) {
                    viewModel.markTaskAsCompleted(task)
                } else {
                    viewModel.markTaskAsUncompleted(task)
                }
            },
            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = task.title,
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = poppinsFamily,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
            ),
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = {
            if (task.isStarred) {
                viewModel.unStarTask(task)
            } else {
                viewModel.starTask(task)
            }
        }) {
            Icon(
                painter = if (task.isStarred) painterResource(id = R.drawable.star_filled) else painterResource(
                    id = R.drawable.star_unfilled
                ),
                contentDescription = if (task.isStarred) "Unstar task" else "Star task",
                tint = if (task.isStarred) MaterialTheme.colorScheme.primary else Color.Gray
            )
        }
    }
}