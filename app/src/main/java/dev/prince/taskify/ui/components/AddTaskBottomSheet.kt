package dev.prince.taskify.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.prince.taskify.R
import dev.prince.taskify.database.Task
import dev.prince.taskify.ui.home.HomeViewModel
import dev.prince.taskify.ui.theme.poppinsFamily
import kotlinx.coroutines.delay

@Composable
fun AddTaskBottomSheet(
    onDismiss: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var taskTitle by remember { mutableStateOf("") }
    var taskDesc by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    BottomSheet(onDismiss = onDismiss) {
        LaunchedEffect(Unit) {
            delay(300)
            focusRequester.requestFocus()
        }

        BorderlessEditableField(
            value = taskTitle,
            onValueChange = {
                if (it.length <= 30) {
                    taskTitle = it
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            placeholder = "Task Title",
            maxLines = 2,
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = poppinsFamily
            )
        )

        if (viewModel.isTaskDescVisible) {
            BorderlessEditableField(
                value = taskDesc,
                onValueChange = { taskDesc = it },
                modifier = Modifier
                    .fillMaxWidth(),
                placeholder = "Add details",
                maxLines = 5,
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = poppinsFamily
                )
            )
        }

        Row {
            IconButton(
                onClick = {
                    viewModel.isTaskDescVisible = true
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.details),
                    contentDescription = "Add details",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    if (taskTitle.isNotBlank()) {
                        viewModel.addTask(
                            Task(
                                title = taskTitle,
                                isCompleted = false,
                                isStarred = false,
                                description = taskDesc
                            )
                        )
                        viewModel.isTaskDescVisible = false
                        onDismiss()
                        keyboardController?.hide()
                    }
                }
            ) {
                Text(
                    text = "Add Task",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = poppinsFamily
                    )
                )
            }
        }
    }
}