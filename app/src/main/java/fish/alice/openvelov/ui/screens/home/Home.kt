package fish.alice.openvelov.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import fish.alice.openvelov.ui.navigation.Permissions
import fish.alice.openvelov.utils.hasAllPermissions
import androidx.lifecycle.compose.currentStateAsState
import fish.alice.openvelov.data.auth.AuthRepository
import fish.alice.openvelov.data.auth.AuthViewModel

@Composable
fun HomeScreen(
    onMissingPermissions: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    ) {
    // check permissions when coming back from settings or wherever
    val context = LocalContext.current

    LifecycleResumeEffect(Unit) {
        if (!hasAllPermissions(context)) onMissingPermissions()
        onPauseOrDispose { }
    }

    Column {
        Text(text = "Home Screen")
        Button(onClick = { authViewModel.logout() }) {
            Text(text = "Logout")
        }
    }
}