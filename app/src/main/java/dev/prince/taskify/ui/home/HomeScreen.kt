package dev.prince.taskify.ui.home

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.identity.Identity
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.prince.taskify.database.Task
import dev.prince.taskify.signin.GoogleAuthUiClient
import dev.prince.taskify.ui.destinations.SingInScreenDestination
import dev.prince.taskify.ui.theme.Orange
import dev.prince.taskify.ui.theme.poppinsFamily
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@RootNavGraph(start = true)
@Composable
@Destination
fun HomeScreen(
    navigator: DestinationsNavigator,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = context,
            oneTapClient = Identity.getSignInClient(context)
        )
    }

    if (googleAuthUiClient.getSignedInUser() == null) {
        navigator.navigate(SingInScreenDestination)
    }

    val pagerState = rememberPagerState(pageCount = { 2 })
    val allTasks by viewModel.tasks.collectAsState(initial = emptyList())
    val starredTasks by viewModel.starredTasks.collectAsState(initial = emptyList())
    var showAddTaskSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(pagerState.currentPage) {
        selectedTab = pagerState.currentPage
    }


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTaskSheet = true },
                containerColor = Orange
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Task")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp),
                text = "Taskify",
                fontWeight = FontWeight.SemiBold,
                style = TextStyle(
                    fontSize = 26.sp,
                    fontFamily = poppinsFamily,
                    color = Orange
                )
            )

            TabRow(
                selectedTabIndex = selectedTab,
                divider = { },
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Orange,
                        )
                    }
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        scope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    },
                    text = {
                        Text(
                            text = "My Tasks",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = poppinsFamily
                            )
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                    text = {
                        Text(
                            text = "Starred",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = poppinsFamily
                            )
                        )
                    }
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                when (page) {
                    0 -> TaskList(
                        tasks = allTasks,
                        onTaskCompleted = viewModel::toggleTaskCompleted,
                        onTaskStarred = viewModel::toggleTaskStarred
                    )

                    1 -> TaskList(
                        tasks = starredTasks,
                        onTaskCompleted = viewModel::toggleTaskCompleted,
                        onTaskStarred = viewModel::toggleTaskStarred
                    )
                }
            }
        }
    }

    if (showAddTaskSheet) {
        AddTaskBottomSheet(
            onDismiss = { showAddTaskSheet = false },
            onTaskAdded = { task ->
                viewModel.addTask(task)
                showAddTaskSheet = false
            }
        )
    }

    BackHandler {
        if (showAddTaskSheet) {
            showAddTaskSheet = false
        } else {
            (context as ComponentActivity).finish()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskBottomSheet(
    onDismiss: () -> Unit,
    onTaskAdded: (Task) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var taskTitle by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = {
            keyboardController?.hide()
            onDismiss()
        },
        sheetState = rememberModalBottomSheetState(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = taskTitle,
                onValueChange = { taskTitle = it },
                label = { Text("Task title") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            Button(
                onClick = {
                    if (taskTitle.isNotBlank()) {
                        onTaskAdded(Task(title = taskTitle, isCompleted = false, isStarred = false))
                        keyboardController?.hide()
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Add Task")
            }
        }
    }
}

@Composable
fun TaskList(
    tasks: List<Task>,
    onTaskCompleted: (Task) -> Unit,
    onTaskStarred: (Task) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        items(tasks) { task ->
            TaskItem(
                task = task,
                onTaskCompleted = { onTaskCompleted(task) },
                onTaskStarred = { onTaskStarred(task) }
            )
        }
    }
}


@Composable
fun TaskItem(
    task: Task,
    onTaskCompleted: () -> Unit,
    onTaskStarred: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = task.isCompleted,
            onClick = onTaskCompleted,
            colors = RadioButtonDefaults.colors(selectedColor = Orange)
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
        IconButton(onClick = onTaskStarred) {
            Icon(
                imageVector = if (task.isStarred) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = if (task.isStarred) "Unstar task" else "Star task",
                tint = if (task.isStarred) Orange else Color.Gray
            )
        }
    }
}