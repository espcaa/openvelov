package fish.alice.openvelov.ui.screens.login

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fish.alice.openvelov.data.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri

sealed interface LoginUiState {
    data object Idle: LoginUiState
    data object Loading: LoginUiState
    data object Success: LoginUiState
    data class Error(val message: String): LoginUiState
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepo: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState = _uiState.asStateFlow()

    suspend fun buildAuthUrl(): String {
        _uiState.value = LoginUiState.Loading
        return authRepo.buildAuthUrl()
    }

    fun onCodeCaptured(code: String, state: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            runCatching { authRepo.handleAuthCode(code, state) }
                .onSuccess { _uiState.value = LoginUiState.Success }
                .onFailure { _uiState.value = LoginUiState.Error(it.message ?: "Login failed") }
        }
    }

    fun onError(t: Throwable) {
        _uiState.value = LoginUiState.Error(t.message ?: "Login failed")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var authUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state) {
        if (state is LoginUiState.Success) onLoginSuccess()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {

        if (authUrl != null) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Login") },
                        navigationIcon = {
                            Button(onClick = { authUrl = null }) {
                                Text("Cancel")
                            }
                        },
                        actions = {
                            if (state is LoginUiState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .padding(end = 16.dp)
                                        .size(24.dp),
                                    strokeWidth = 2.dp,
                                )
                            }
                        }
                    )
                }
            ) { innerPadding ->
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            webViewClient = object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(
                                    view: WebView, request: WebResourceRequest
                                ): Boolean {
                                    val url = request.url.toString()
                                    val redirectPrefix = "https://velov.grandlyon.com"
                                    if (url.startsWith(redirectPrefix)) {
                                        val uri = url.toUri()
                                        val code = uri.getQueryParameter("code")
                                        val returnedState = uri.getQueryParameter("state")
                                        if (code != null && returnedState != null) {
                                            viewModel.onCodeCaptured(code, returnedState)
                                        }
                                        return true
                                    }
                                    return false
                                }
                            }
                            settings.javaScriptEnabled = true
                            loadUrl(authUrl!!)
                        }
                    },
                    modifier = Modifier.fillMaxSize().padding(innerPadding)
                )
            }
        } else {


            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(text = "Vélo'v", style = MaterialTheme.typography.headlineMedium)

                when (val s = state) {
                    is LoginUiState.Loading -> CircularProgressIndicator(Modifier.padding(top = 24.dp))
                    is LoginUiState.Error -> Text(
                        text = s.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 16.dp),
                    )

                    else -> {}
                }

                Button(
                    enabled = state !is LoginUiState.Loading,
                    modifier = Modifier.padding(top = 24.dp),
                    onClick = {
                        scope.launch {
                            runCatching { viewModel.buildAuthUrl() }
                                .onSuccess { authUrl = it }
                                .onFailure { viewModel.onError(it) }
                        }
                    },
                ) { Text("Sign in") }
            }
        }
    }
}