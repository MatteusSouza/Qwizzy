package com.example.askceny.domain.repositories

import com.example.askceny.domain.models.User
import com.example.askceny.domain.types.AuthState

interface AuthRepository {
    suspend fun createUserWithEmailAndPassword(displayName: String, email: String, password: String): AuthState =
        signUpWithEmail(displayName, email, password)

    suspend fun signInWithEmailAndPassword(email: String, password: String): AuthState =
        signInWithEmail(email, password)

    suspend fun signUpWithEmail(displayName: String, email: String, password: String): AuthState
    suspend fun signInWithEmail(email: String, password: String): AuthState
    suspend fun signUpWithGoogle(idToken: String, nonce: String? = null): AuthState
    suspend fun signInWithGoogle(idToken: String, nonce: String? = null): AuthState
    suspend fun verifyEmailOtp(email: String, token: String): AuthState
    suspend fun resendSignUpEmailOtp(email: String): AuthState
    fun getCurrentUser() : User?
    fun signOut()
}
