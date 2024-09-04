package dev.prince.taskify.ui.home

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.identity.Identity
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.prince.taskify.R
import dev.prince.taskify.signin.GoogleAuthUiClient
import dev.prince.taskify.ui.components.AddTaskBottomSheet
import dev.prince.taskify.ui.components.TaskList
import dev.prince.taskify.ui.destinations.SignInScreenDestination
import dev.prince.taskify.ui.theme.poppinsFamily
import dev.prince.taskify.util.LocalSnackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@RootNavGraph(start = true)
@Composable
@Destination
fun HomeScreen(
    navigator: DestinationsNavigator,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val snackbar = LocalSnackbar.current
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.messages.collect {
            snackbar(it)
        }
    }

    LaunchedEffect(true) {
        delay(400)
        isLoading = false
    }

    val context = LocalContext.current

    val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = context,
            oneTapClient = Identity.getSignInClient(context)
        )
    }

    if (googleAuthUiClient.getSignedInUser() == null) {
        navigator.navigate(SignInScreenDestination)
    }

    val pagerState = rememberPagerState(pageCount = { 2 })
    val allTasks by viewModel.tasks.collectAsState(initial = emptyList())
    val starredTasks by viewModel.starredTasks.collectAsState(initial = emptyList())
    var showAddTaskSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(pagerState.currentPage) {
        selectedTab = pagerState.currentPage
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        var fabHeight by remember { mutableStateOf(0.dp) }
        Column(
            modifier = Modifier
                .fillMaxSize()
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

            Spacer(modifier = Modifier.height(16.dp))

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
                    0 -> {
                        if (isLoading) {
                            ProgressIndicator()
                        } else if (allTasks.isEmpty()) {
                            EmptyStateMessage("No Tasks Added!")
                        } else {
                            val sortedTasks = allTasks.sortedBy { it.isCompleted }
                            TaskList(
                                navigator = navigator,
                                tasks = sortedTasks,
                                bottomPadding = fabHeight + 16.dp
                            )
                        }
                    }

                    1 -> {
                        if (isLoading) {
                            ProgressIndicator()
                        } else if (starredTasks.isEmpty()) {
                            EmptyStateMessage("No Tasks Starred!")
                        } else {
                            TaskList(
                                navigator = navigator,
                                tasks = starredTasks,
                                bottomPadding = fabHeight + 16.dp
                            )
                        }
                    }
                }
            }
        }

        with(LocalDensity.current) {
            FloatingActionButton(
                onClick = { showAddTaskSheet = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(vertical = 16.dp, horizontal = 16.dp)
                    .onPlaced {
                        fabHeight = it.size.height.toDp()
                    },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add),
                    contentDescription = "Add Task",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }

    if (showAddTaskSheet) {
        AddTaskBottomSheet(
            onDismiss = { showAddTaskSheet = false }
        )
    }

    BackHandler {
        if (selectedTab == 1) {
            selectedTab = 0
            scope.launch {
                pagerState.animateScrollToPage(0)
            }
        } else if (showAddTaskSheet) {
            showAddTaskSheet = false
        } else {
            (context as ComponentActivity).finish()
        }
    }
}


@Composable
fun EmptyStateMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground,
                fontFamily = poppinsFamily
            )
        )
    }
}

@Composable
fun ProgressIndicator() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .size(36.dp)
        )
    }
}