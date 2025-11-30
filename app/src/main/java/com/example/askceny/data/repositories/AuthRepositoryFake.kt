package com.example.askceny.data.repositories

import com.example.askceny.data.local.MockedAuthManager
import com.example.askceny.data.models.User
import com.example.askceny.data.types.AuthState
import com.example.askceny.data.types.ErrorCode
import com.example.askceny.exceptions.AuthException

class AuthRepositoryFake : AuthRepository {

    private val authManager = MockedAuthManager()

    override suspend fun createUserWithEmailAndPassword(
        displayName: String,
        email: String,
        password: String
    ): AuthState {
        try {
            authManager.signUpWithEmail(email = email, displayName = displayName, username = "", password = password)
            return AuthState.Authenticated
        } catch (e: AuthException) {
            return AuthState.AuthError(e.authException)
        } catch (e: Exception) {
            return AuthState.AuthError(ErrorCode.UNKNOWN_ERROR)
        }
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): AuthState {
        try {
            authManager.login(email,password)
            return AuthState.Authenticated
        } catch (e: AuthException) {
            return AuthState.AuthError(e.authException)
        }
        catch (e: Exception) {
            return AuthState.AuthError(ErrorCode.UNKNOWN_ERROR)
        }
    }

    override fun getCurrentUser(): User? {
        return authManager.getUser()
    }

    override fun signOut() {
        authManager.logoff()
    }
}