package fish.alice.openvelov

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import fish.alice.openvelov.ui.navigation.AppNavHost
import fish.alice.openvelov.ui.navigation.Onboarding
import fish.alice.openvelov.ui.navigation.Splash
import fish.alice.openvelov.ui.theme.OpenvelovTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OpenvelovTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { paddingValues ->
                    AppNavHost(
                        startDestination = Splash,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}
