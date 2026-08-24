package fish.alice.openvelov.ui.screens.login

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    Column() {
        Text(text = "Login Screen")
        Button(onClick = onLoginSuccess) {
            Text(text = "Login")
        }
    }
}