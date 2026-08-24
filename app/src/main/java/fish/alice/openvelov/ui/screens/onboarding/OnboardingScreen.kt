package fish.alice.openvelov.ui.screens.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    var step by rememberSaveable { mutableIntStateOf(0) }
    BackHandler(enabled = step > 0) {
        step=0
    }
    when (step) {
        0 -> WelcomePage(onContinue = { step = 1 })
        else -> PermissionPage(
            onPermissionGranted = onFinish,
            onBack = { step = 0 },
        )
    }
}