package fish.alice.openvelov.ui.screens.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import fish.alice.openvelov.data.auth.AuthViewModel

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    Scaffold() { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Button(
                onClick = { authViewModel.logout() },
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError,
                    disabledContainerColor = MaterialTheme.colorScheme.onError.copy(alpha = 0.12f),
                    disabledContentColor = MaterialTheme.colorScheme.onError.copy(alpha = 0.38f)
                )
            ) {
                Text(text = "Logout")
            }
        }
    }
}