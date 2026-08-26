package fish.alice.openvelov.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fish.alice.openvelov.data.auth.UserTokenSource
import fish.alice.openvelov.data.auth.WebTokenSource
import fish.alice.openvelov.data.remote.AuthInterceptor
import fish.alice.openvelov.data.remote.BikesApi
import fish.alice.openvelov.data.remote.StationsApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton


@Qualifier
@Retention(AnnotationRetention.BINARY) annotation class WebApi
@Qualifier @Retention(AnnotationRetention.BINARY) annotation class UserApi

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private val json = Json { ignoreUnknownKeys = true }

    private fun okHttp(source: fish.alice.openvelov.data.auth.TokenSource) =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
                redactHeader("Authorization")
            })
            .addInterceptor(AuthInterceptor(source))
            .build()

    private fun retrofit(client: OkHttpClient) =
        Retrofit.Builder()
            .baseUrl("https://api.cyclocity.fr/")
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides @Singleton @WebApi
    fun webClient(takn: WebTokenSource) = okHttp(takn)

    @Provides @Singleton @UserApi
    fun userClient(user: UserTokenSource) = okHttp(user)

    @Provides @Singleton @WebApi
    fun webRetrofit(@WebApi client: OkHttpClient) = retrofit(client)

    @Provides @Singleton @UserApi
    fun userRetrofit(@UserApi client: OkHttpClient) = retrofit(client)

    @Provides @Singleton
    fun bikesApi(@WebApi retrofit: Retrofit): BikesApi = retrofit.create(BikesApi::class.java)

    @Provides @Singleton
    fun stationsApi(@WebApi retrofit: Retrofit): StationsApi = retrofit.create(StationsApi::class.java)
}