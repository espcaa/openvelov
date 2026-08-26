package fish.alice.openvelov.data.auth

import kotlinx.coroutines.suspendCancellableCoroutine
import net.openid.appauth.AuthorizationService
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class UserTokenSource(
    private val repo: AuthRepository,
    private val authService: AuthorizationService,
) : TokenSource {

    class NotLoggedInException : Exception("not logged in")

    override suspend fun authHeader(): String {
        val state = repo.currentState() ?: throw NotLoggedInException()
        val accessToken = suspendCancellableCoroutine { cont ->
            state.performActionWithFreshTokens(authService) { token, _, ex ->
                if (token != null) cont.resume(token)
                else cont.resumeWithException(ex ?: RuntimeException("token refresh failed"))
            }
        }
        repo.persist(state)
        return "Bearer $accessToken"
    }

    override suspend fun invalidate() {
        repo.logout()
    }
}