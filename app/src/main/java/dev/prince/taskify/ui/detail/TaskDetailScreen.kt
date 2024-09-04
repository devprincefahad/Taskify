package dev.prince.taskify.ui.detail

import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.prince.taskify.R
import dev.prince.taskify.ui.components.BorderlessEditableField
import dev.prince.taskify.ui.theme.poppinsFamily
import dev.prince.taskify.util.LocalSnackbar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination
fun TaskDetailScreen(
    navigator: DestinationsNavigator,
    taskId: Int,
    viewModel: TaskViewModel = hiltViewModel()
) {

    val snackbar = LocalSnackbar.current

    LaunchedEffect(Unit) {
        viewModel.messages.collect {
            snackbar(it)
        }
    }

    val task = viewModel.getTaskById(taskId).collectAsState(initial = null).value

    if (task != null) {
        var title by remember { mutableStateOf(task.title) }
        var description by remember { mutableStateOf(task.description ?: "") }
        var isStarred by remember { mutableStateOf(task.isStarred) }
        var showMenu by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TopAppBar(
                windowInsets = WindowInsets(0, 0, 0, 0),
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navigator.popBackStack()
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_back),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            isStarred = !isStarred
                            viewModel.updateTask(task.copy(isStarred = isStarred))
                        }
                    ) {
                        Icon(
                            painter = if (isStarred) painterResource(id = R.drawable.star_filled)
                            else painterResource(id = R.drawable.star_unfilled),
                            contentDescription = if (isStarred) "Unstar task" else "Star task",
                            tint = if (isStarred) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(
                            painter = painterResource(id = R.drawable.more_vert),
                            contentDescription = "More options"
                        )
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            onClick = {
                                viewModel.deleteTask(task)
                                navigator.popBackStack()
                            },
                            text = {
                                Text(
                                    text = "Delete",
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = poppinsFamily
                                    )
                                )
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )

            Spacer(modifier = Modifier.height(16.dp))

            BorderlessEditableField(
                value = title,
                onValueChange = { newTitle ->
                    title = newTitle
                    viewModel.updateTask(task.copy(title = newTitle))
                },
                placeholder = "Enter title",
                textStyle = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = poppinsFamily
                ),
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(8.dp))

            BorderlessEditableField(
                value = description,
                onValueChange = { newDescription ->
                    description = newDescription
                    viewModel.updateTask(task.copy(description = newDescription))
                },
                placeholder = "Add details",
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    fontFamily = poppinsFamily
                ),
                maxLines = 20
            )
        }
    }

    BackHandler {
        navigator.popBackStack()
    }
}
