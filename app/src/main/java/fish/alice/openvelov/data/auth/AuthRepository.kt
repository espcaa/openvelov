package fish.alice.openvelov.data.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import net.openid.appauth.AuthorizationService
import androidx.core.net.toUri
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.TokenRequest
import net.openid.appauth.TokenResponse
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import android.util.Base64
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.security.MessageDigest
import java.security.SecureRandom


private val Context.authDataStore by preferencesDataStore("auth")
private val AUTH_STATE_KEY = stringPreferencesKey("auth_state")

class AuthRepository(
    private val context: Context,
    private val authService: AuthorizationService,
) {

    private val _logoutEvents = MutableSharedFlow<Unit>()
    val logoutEvents = _logoutEvents.asSharedFlow()

    private val cipher = TokenCipher()
    private val issuer = "https://iam.cyclocity.fr/realms/vls-default".toUri()
    private val clientId = "vls-android-lyon"
    private val redirectUri = "https://velov.grandlyon.com/openid_connect_login".toUri()

    private var codeVerifier: String = ""
    private var authState: String = ""

    val isLoggedIn: Flow<Boolean> = context.authDataStore.data.map { prefs ->
        prefs[AUTH_STATE_KEY]?.let {
            try {
                val state = AuthState.jsonDeserialize(cipher.decrypt(it))
                state.isAuthorized && state.authorizationServiceConfiguration != null
            } catch (_: Exception) {
                false
            }
        } ?: false
    }

    suspend fun currentState(): AuthState? = readState().takeIf { it?.isAuthorized ?: false }

    suspend fun persist(state: AuthState) = writeState(state)

    private suspend fun readState(): AuthState? {
        val stored = context.authDataStore.data.first()[AUTH_STATE_KEY] ?: return null
        return try {
            AuthState.jsonDeserialize(cipher.decrypt(stored)).takeIf {
                it.authorizationServiceConfiguration != null
            }
        } catch (_: Exception) {
            null
        }
    }

    private suspend fun writeState(state: AuthState) {
        context.authDataStore.edit { it[AUTH_STATE_KEY] = cipher.encrypt(state.jsonSerializeString()) }
    }

    private suspend fun fetchConfig(): AuthorizationServiceConfiguration =
        suspendCancellableCoroutine { cont ->
            AuthorizationServiceConfiguration.fetchFromIssuer(issuer) { config, ex ->
                if (config != null) cont.resume(config)
                else cont.resumeWithException(ex ?: RuntimeException("discovery failed"))
            }
        }

    suspend fun buildAuthUrl(): String {
        val config = fetchConfig()

        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)
        codeVerifier = Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)

        val digest = MessageDigest.getInstance("SHA-256").digest(codeVerifier.toByteArray())
        val codeChallenge = Base64.encodeToString(digest, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)

        val stateBytes = ByteArray(16)
        SecureRandom().nextBytes(stateBytes)
        authState = Base64.encodeToString(stateBytes, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)

        return "${config.authorizationEndpoint}" +
                "?client_id=$clientId" +
                "&redirect_uri=${Uri.encode(redirectUri.toString())}" +
                "&response_type=code" +
                "&scope=openid%20email" +
                "&code_challenge=$codeChallenge" +
                "&code_challenge_method=S256" +
                "&state=$authState"
    }

    suspend fun handleAuthCode(code: String, returnedState: String) {
        if (returnedState != authState) throw RuntimeException("state mismatch — possible CSRF")

        val config = fetchConfig()
        val request = TokenRequest.Builder(config, clientId)
            .setAuthorizationCode(code)
            .setRedirectUri(redirectUri)
            .setCodeVerifier(codeVerifier)
            .build()

        val tokenResp = performTokenRequest(request)

        val state = AuthState(config)
        state.update(tokenResp, null)
        writeState(state)
    }

    private suspend fun performTokenRequest(req: TokenRequest): TokenResponse =
        suspendCancellableCoroutine { cont ->
            authService.performTokenRequest(req) { resp, ex ->
                if (resp != null) cont.resume(resp)
                else cont.resumeWithException(ex ?: RuntimeException("token exchange failed"))
            }
        }

    suspend fun getFreshAccessToken(): String {
        val state = readState() ?: run {
            logout()
            throw RuntimeException("User is not logged in or auth state is invalid")
        }

        return try {
            val token = suspendCancellableCoroutine { cont ->
                state.performActionWithFreshTokens(authService) { accessToken, _, ex ->
                    if (accessToken != null) {
                        cont.resume(accessToken)
                    } else {
                        cont.resumeWithException(ex ?: RuntimeException("Failed to acquire fresh token"))
                    }
                }
            }
            writeState(state)
            token
        } catch (e: Exception) {
            logout()
            throw e
        }
    }

    suspend fun logout() {
        context.authDataStore.edit { it.remove(AUTH_STATE_KEY) }
        _logoutEvents.emit(Unit)
    }
}