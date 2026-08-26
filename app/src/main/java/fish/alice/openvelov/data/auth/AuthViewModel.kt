package fish.alice.openvelov.data.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepository
) : ViewModel() {

    val logoutEvents = authRepo.logoutEvents

    suspend fun isLoggedIn(): Boolean = authRepo.isLoggedIn.first()

    fun logout() {
        viewModelScope.launch { authRepo.logout() }
    }

    fun get_token() {
        viewModelScope.launch {
            try {
                authRepo.getFreshAccessToken()
            } catch (e: Exception) {
                println("Token refresh failed: ${e.message}")
            }
        }
    }
}