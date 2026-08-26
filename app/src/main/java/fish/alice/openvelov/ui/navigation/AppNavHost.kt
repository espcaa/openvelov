package fish.alice.openvelov.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fish.alice.openvelov.data.auth.AuthRepository
import fish.alice.openvelov.data.auth.AuthViewModel
import fish.alice.openvelov.ui.screens.home.HomeScreen
import fish.alice.openvelov.ui.screens.login.LoginScreen
import fish.alice.openvelov.ui.screens.onboarding.OnboardingScreen
import fish.alice.openvelov.ui.screens.onboarding.PermissionScreen
import fish.alice.openvelov.ui.screens.splash.SplashScreen
import fish.alice.openvelov.utils.hasAllPermissions
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val DURATION = 300

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    startDestination: Any,
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel(),
) {

    LaunchedEffect(Unit) {
        authViewModel.logoutEvents.collect {
            navController.navigate(Login) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { fadeIn(tween(DURATION)) + slideInHorizontally(tween(DURATION)) { it / 4 } },
        exitTransition = { fadeOut(tween(DURATION)) + slideOutHorizontally(tween(DURATION)) { -it / 4 } },
        popEnterTransition = { fadeIn(tween(DURATION)) + slideInHorizontally(tween(DURATION)) { -it / 4 } },
        popExitTransition = { fadeOut(tween(DURATION)) + slideOutHorizontally(tween(DURATION)) { it / 4 } },
    ) {
        composable<Onboarding> { OnboardingScreen(onNext = {
            // check if permissions are alr granted
            if (hasAllPermissions(navController.context)) {
                navController.navigate(Login)
            } else {
                navController.navigate(Permissions)
            }
        }) }
        composable<Permissions> {
            val scope = rememberCoroutineScope()
            PermissionScreen(onNext = {
                scope.launch {
                    if (authViewModel.isLoggedIn()) {
                        navController.navigate(Home)
                    } else {
                        navController.navigate(Login)
                    }
                }
            })
        }
        composable<Home> { HomeScreen( onMissingPermissions = { navController.navigate(Permissions) }) }
        composable<Login> {
            LoginScreen(onLoginSuccess = {
                navController.navigate(Home) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            })
        }
        composable<Splash> { SplashScreen(navController) }
    }
}