package dev.prince.taskify.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.identity.Identity
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.prince.taskify.signin.GoogleAuthUiClient
import dev.prince.taskify.ui.destinations.SingInScreenDestination

@RootNavGraph(start = true)
@Composable
@Destination
fun HomeScreen(
    navigator: DestinationsNavigator
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

}