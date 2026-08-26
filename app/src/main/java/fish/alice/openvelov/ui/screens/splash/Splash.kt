package fish.alice.openvelov.ui.screens.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import fish.alice.openvelov.data.auth.AuthRepository
import fish.alice.openvelov.data.auth.AuthViewModel
import fish.alice.openvelov.ui.navigation.Home
import fish.alice.openvelov.ui.navigation.Onboarding
import fish.alice.openvelov.ui.navigation.Permissions
import fish.alice.openvelov.utils.hasAllPermissions

@Composable
fun SplashScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val loggedIn = authViewModel.isLoggedIn()
        val permsGranted = hasAllPermissions(context)

        val destination = when {
            !loggedIn      -> Onboarding
            !permsGranted  -> Permissions
            else          -> Home
        }

        navController.navigate(destination) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }
}