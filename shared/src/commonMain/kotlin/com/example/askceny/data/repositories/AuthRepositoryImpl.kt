package com.example.askceny.data.repositories

import com.example.askceny.data.remote.api.AuthRemoteDataSource
import com.example.askceny.data.remote.api.AuthRemoteResult
import com.example.askceny.data.remote.api.SupabaseAuthRemoteDataSource
import com.example.askceny.domain.models.User
import com.example.askceny.domain.repositories.AuthRepository
import com.example.askceny.domain.types.AuthState

class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource = SupabaseAuthRemoteDataSource(),
) : AuthRepository {
    internal val isUsingPlaceholderAuthClient: Boolean
        get() = remoteDataSource is SupabaseAuthRemoteDataSource &&
            remoteDataSource.isUsingPlaceholderClient

    override suspend fun signUpWithEmail(displayName: String, email: String, password: String): AuthState {
        return remoteDataSource.signUpWithEmail(displayName, email, password).toAuthState()
    }

    override suspend fun signInWithEmail(email: String, password: String): AuthState {
        return remoteDataSource.signInWithEmail(email, password).toAuthState()
    }

    override suspend fun signUpWithGoogle(idToken: String, nonce: String?): AuthState {
        return remoteDataSource.signUpWithGoogle(idToken, nonce).toAuthState()
    }

    override suspend fun signInWithGoogle(idToken: String, nonce: String?): AuthState {
        return remoteDataSource.signInWithGoogle(idToken, nonce).toAuthState()
    }

    override fun getCurrentUser(): User? {
        return remoteDataSource.getCurrentUser()
    }

    override fun signOut() {
        remoteDataSource.signOut()
    }

    private fun AuthRemoteResult.toAuthState(): AuthState {
        return when (this) {
            is AuthRemoteResult.Authenticated -> AuthState.Authenticated
            is AuthRemoteResult.EmailConfirmationRequired -> AuthState.EmailConfirmationRequired
            is AuthRemoteResult.Failure -> AuthState.AuthError(errorCode)
        }
    }
}
