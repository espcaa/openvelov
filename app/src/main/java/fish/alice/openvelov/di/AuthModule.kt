package fish.alice.openvelov.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fish.alice.openvelov.data.auth.AuthRepository
import net.openid.appauth.AuthorizationService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides @Singleton
    fun provideAuthorizationService(
        @ApplicationContext context: Context,
    ): AuthorizationService = AuthorizationService(context)

    @Provides @Singleton
    fun provideAuthRepository(
        @ApplicationContext context: Context,
        authService: AuthorizationService,
    ): AuthRepository = AuthRepository(context, authService)
}