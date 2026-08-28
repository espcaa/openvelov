package fish.alice.openvelov.ui.screens.main

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import fish.alice.openvelov.utils.hasAllPermissions
import fish.alice.openvelov.ui.maps.MapLibreView
import fish.alice.openvelov.ui.navigation.MapTab
import fish.alice.openvelov.ui.navigation.ProfileTab
import fish.alice.openvelov.ui.navigation.TopLevelDestination
import fish.alice.openvelov.ui.navigation.navigateToTab

@Composable
fun MainScreen(
    onMissingPermissions: () -> Unit,
) {
    val context = LocalContext.current

    LifecycleResumeEffect(Unit) {
        if (!hasAllPermissions(context)) onMissingPermissions()
        onPauseOrDispose {  }
    }

    val tabNav = rememberNavController()
    val backStack by tabNav.currentBackStackEntryAsState()
    val currentDestination = backStack?.destination

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            TopLevelDestination.entries.forEach { dest ->
                val selected = currentDestination?.hierarchy?.any { it.hasRoute(dest.route::class) } == true
                item(
                    selected = selected,
                    onClick = { tabNav.navigateToTab(dest) },
                    icon = {
                        Icon(
                            imageVector = if (selected) dest.selectedIcon else dest.icon,
                            contentDescription = null,
                        )
                    },
                    label = { Text(stringResource(dest.label)) },
                )
            }
        }
    )  {
        Box(Modifier.fillMaxSize()) {
            MapLibreView(
                modifier = Modifier.fillMaxSize(),
            )

            NavHost(
                navController = tabNav,
                startDestination = MapTab,
                enterTransition = { fadeIn(tween(200)) },
                exitTransition = { fadeOut(tween(200)) },
            ) {
                composable<MapTab> { }
                composable<ProfileTab> { ProfileScreen() }
            }
        }
    }
}