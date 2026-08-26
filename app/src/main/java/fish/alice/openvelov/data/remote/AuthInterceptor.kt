package fish.alice.openvelov.data.remote

import fish.alice.openvelov.data.auth.TokenSource
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val source: TokenSource) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        try {
            val headerValue = runBlocking { source.authHeader() }

            if (!headerValue.isNull_or_blank()) {
                requestBuilder.header("Authorization", headerValue)
            }
        } catch (e: Exception) {
            println("AuthInterceptor: Failed to acquire authorization header: ${e.message}")
        }

        val response = chain.proceed(requestBuilder.build())

        if (response.code == 401) {
            try {
                runBlocking { source.invalidate() }
            } catch (e: Exception) {
                println("AuthInterceptor: Failed to invalidate token: ${e.message}")
            }
        }

        return response
    }
}

private fun String?.isNull_or_blank(): Boolean = this == null || this.trim().isEmpty()