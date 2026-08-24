package fish.alice.openvelov.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fish.alice.openvelov.ui.screens.home.HomeScreen
import fish.alice.openvelov.ui.screens.login.LoginScreen
import fish.alice.openvelov.ui.screens.onboarding.OnboardingScreen

private const val DURATION = 300

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    startDestination: Any,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { fadeIn(tween(DURATION)) + slideInHorizontally(tween(DURATION)) { it / 4 } },
        exitTransition = { fadeOut(tween(DURATION)) + slideOutHorizontally(tween(DURATION)) { -it / 4 } },
        popEnterTransition = { fadeIn(tween(DURATION)) + slideInHorizontally(tween(DURATION)) { -it / 4 } },
        popExitTransition = { fadeOut(tween(DURATION)) + slideOutHorizontally(tween(DURATION)) { it / 4 } },
    ) {
        composable<Onboarding> { OnboardingScreen(onFinish = { navController.navigate(Login) }) }
        composable<Home> { HomeScreen() }
        composable<Login> {
            LoginScreen(onLoginSuccess = {
                navController.navigate(Home) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            })
        }
    }
}