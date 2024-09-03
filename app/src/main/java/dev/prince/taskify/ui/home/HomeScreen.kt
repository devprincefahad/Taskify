package dev.prince.taskify.ui.home

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
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
import dev.prince.taskify.R
import dev.prince.taskify.database.Task
import dev.prince.taskify.signin.GoogleAuthUiClient
import dev.prince.taskify.ui.destinations.SingInScreenDestination
import dev.prince.taskify.ui.destinations.TaskDetailScreenDestination
import dev.prince.taskify.ui.detail.BorderlessEditableField
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
        modifier = Modifier.imePadding(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTaskSheet = true },
                containerColor = MaterialTheme.colorScheme.primary
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
                    .padding(horizontal = 16.dp),
                text = "Taskify",
                fontWeight = FontWeight.SemiBold,
                style = TextStyle(
                    fontSize = 26.sp,
                    fontFamily = poppinsFamily,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            TabRow(
                selectedTabIndex = selectedTab,
                divider = { },
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = MaterialTheme.colorScheme.primary
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
                        navigator = navigator,
                        tasks = allTasks
                    )

                    1 -> TaskList(
                        navigator = navigator,
                        tasks = starredTasks
                    )
                }
            }
        }
    }

    if (showAddTaskSheet) {
        AddTaskBottomSheet(
            onDismiss = { showAddTaskSheet = false }
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
    viewModel: HomeViewModel = hiltViewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var taskTitle by remember { mutableStateOf("") }
    var taskDesc by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = {
            keyboardController?.hide()
            onDismiss()
        },
        sheetState = rememberModalBottomSheetState(),
        windowInsets = WindowInsets.ime,
        dragHandle = {}
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            BorderlessEditableField(
                value = taskTitle,
                onValueChange = {
                    if (it.length <= 30) {
                        taskTitle = it
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                placeholder = "Task Title",
                maxLines = 2,
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            if (viewModel.isTaskDescVisible) {
                BorderlessEditableField(
                    value = taskDesc,
                    onValueChange = { taskDesc = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    placeholder = "Add details",
                    maxLines = 5,
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
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
                    Text("Add Task")
                }
            }

        }
    }
}

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

@Composable
fun TaskItem(
    navigator: DestinationsNavigator,
    task: Task,
    viewModel: HomeViewModel = hiltViewModel()
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navigator.navigate(TaskDetailScreenDestination(task.id))
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = task.isCompleted,
            onClick = {
                if (task.isCompleted) {
                    viewModel.markTaskAsUncompleted(task)
                } else {
                    viewModel.markTaskAsCompleted(task)
                }
            },
            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
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