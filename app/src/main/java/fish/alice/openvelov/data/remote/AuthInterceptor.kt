package fish.alice.openvelov.data.remote

import fish.alice.openvelov.data.auth.AuthRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val repo: AuthRepository,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { repo.getFreshAccessToken() }
        val request = chain.request().newBuilder()
            .header("Authorization", "vls.taknv1 $token")
            .build()

        val response = chain.proceed(request)

        if (response.code == 401) {
            runBlocking { repo.logout() }
        }

        return response
    }
}