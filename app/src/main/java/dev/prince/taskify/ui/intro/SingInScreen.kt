package dev.prince.taskify.ui.intro

import android.app.Activity.RESULT_OK
import android.content.Context
import android.net.ConnectivityManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.identity.Identity
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.prince.taskify.signin.GoogleAuthUiClient
import dev.prince.taskify.ui.destinations.HomeScreenDestination
import dev.prince.taskify.ui.theme.poppinsFamily
import dev.prince.taskify.util.LocalSnackbar
import kotlinx.coroutines.launch

@Destination
@Composable
fun SingInScreen(
    viewModel: SignInViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val snackbar = LocalSnackbar.current

    LaunchedEffect(Unit) {
        viewModel.messages.collect {
            snackbar(it)
        }
    }

    val context = LocalContext.current

    val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = context,
            oneTapClient = Identity.getSignInClient(context)
        )
    }

    val state by viewModel.state.collectAsState()
    LaunchedEffect(key1 = Unit) {
        if (googleAuthUiClient.getSignedInUser() != null) {
            navigator.navigate(
                HomeScreenDestination
            )
        }
    }

    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == RESULT_OK) {
                scope.launch {
                    val signInResult = googleAuthUiClient.signInWithIntent(
                        intent = result.data ?: return@launch
                    )
                    viewModel.onSignInResult(signInResult)
                }
            }
        }
    )

    LaunchedEffect(key1 = state.isSignInSuccessful) {
        if (state.isSignInSuccessful) {
            viewModel.showSnackBar("Sign in successful")

            navigator.navigate(
                HomeScreenDestination
            )
            viewModel.resetState()
        }
    }

    SignInScreenContent(
        navigator = navigator,
        googleAuthUiClient = googleAuthUiClient,
        launcher = launcher
    )

    BackHandler {
        (context as ComponentActivity).finish()
    }

}


@Composable
fun SignInScreenContent(
    viewModel: SignInViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
    googleAuthUiClient: GoogleAuthUiClient,
    launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>
) {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(
                    top = 16.dp,
                    end = 16.dp
                )
                .fillMaxWidth(),
            text = "Organize your tasks at one place",
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            style = TextStyle(
                fontSize = 42.sp,
                fontFamily = poppinsFamily,
                lineHeight = 42.sp
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth(),
            onClick = {
                if (viewModel.isNetworkConnected(connectivityManager)) {
                    scope.launch {
                        val signInIntentSender = googleAuthUiClient.signIn()
                        launcher.launch(
                            IntentSenderRequest.Builder(
                                signInIntentSender ?: return@launch
                            ).build()
                        )
                    }
                } else {
                    viewModel.showSnackBar("Not connected to the internet")
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Get Started",
                fontWeight = FontWeight.Bold,
                style = TextStyle(
                    fontSize = 22.sp,
                    fontFamily = poppinsFamily,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }

        Spacer(modifier = Modifier.navigationBarsPadding())

    }
}