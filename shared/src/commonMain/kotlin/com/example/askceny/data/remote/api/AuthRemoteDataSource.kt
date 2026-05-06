package com.example.askceny.data.remote.api

import com.example.askceny.domain.models.User
import com.example.askceny.domain.types.ErrorCode

interface AuthRemoteDataSource {
    suspend fun signUpWithEmail(displayName: String, email: String, password: String): AuthRemoteResult
    suspend fun signInWithEmail(email: String, password: String): AuthRemoteResult
    suspend fun signUpWithGoogle(idToken: String, nonce: String? = null): AuthRemoteResult
    suspend fun signInWithGoogle(idToken: String, nonce: String? = null): AuthRemoteResult
    fun getCurrentUser(): User?
    fun signOut()
}

sealed class AuthRemoteResult {
    data class Authenticated(val user: User? = null): AuthRemoteResult()
    data class EmailConfirmationRequired(val user: User? = null): AuthRemoteResult()
    data class Failure(val errorCode: ErrorCode): AuthRemoteResult()
}

class SupabaseAuthRemoteDataSource : AuthRemoteDataSource {
    override suspend fun signUpWithEmail(displayName: String, email: String, password: String): AuthRemoteResult {
        TODO("Supabase email sign-up not yet implemented")
    }

    override suspend fun signInWithEmail(email: String, password: String): AuthRemoteResult {
        TODO("Supabase email sign-in not yet implemented")
    }

    override suspend fun signUpWithGoogle(idToken: String, nonce: String?): AuthRemoteResult {
        TODO("Supabase Google sign-up not yet implemented")
    }

    override suspend fun signInWithGoogle(idToken: String, nonce: String?): AuthRemoteResult {
        TODO("Supabase Google sign-in not yet implemented")
    }

    override fun getCurrentUser(): User? {
        TODO("Supabase current user lookup not yet implemented")
    }

    override fun signOut() {
        TODO("Supabase sign-out not yet implemented")
    }
}
