package fish.alice.openvelov.data.auth

interface TokenSource {
    // provides a different auth header depending on the web/android api needed
    suspend fun authHeader(): String
    suspend fun invalidate() {}
}