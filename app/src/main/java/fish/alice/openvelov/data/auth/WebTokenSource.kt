package fish.alice.openvelov.data.auth

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.OkHttpClient
import fish.alice.openvelov.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class WebTokenSource (
    private val http: OkHttpClient = OkHttpClient()
) : TokenSource {
    @Volatile private var cached: String? = null
    private val mutex = Mutex()

    override suspend fun authHeader(): String {
        cached?.let { return "Taknv1 $it" }
        return mutex.withLock {
            cached?.let { return "Taknv1 $it" }
            val token = mint()
            cached = token
            "Taknv1 $token"
        }
    }

    override suspend fun invalidate() {
        cached = null
    }

    private suspend fun mint(): String {
        val body = """{"code":"vls.web.lyon:PRD","key":"${BuildConfig.VELOV_CLIENT_KEY}"}"""
            .toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("https://api.cyclocity.fr/auth/environments/PRD/client_tokens")
            .header("refreshToken", BuildConfig.VELOV_REFRESH_TOKEN)
            .header("Origin", "https://velov.grandlyon.com")
            .header("Referer", "https://velov.grandlyon.com/mapping")
            .post(body)
            .build()
        return withContext(Dispatchers.IO) {
            http.newCall(request).execute().use { resp ->
                check(resp.isSuccessful) { "mint failed: HTTP ${resp.code}" }
                Json.parseToJsonElement(resp.body.string())
                    .jsonObject["accessToken"]
                    ?.jsonPrimitive?.content
                    ?: error("client_tokens response missing accessToken")
            }
        }
    }
}