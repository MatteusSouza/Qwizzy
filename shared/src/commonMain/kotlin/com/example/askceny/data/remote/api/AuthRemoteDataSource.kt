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

interface SupabaseAuthSessionClient {
    fun currentUserOrNull(): SupabaseAuthUser?
    fun signOut()
}

data class SupabaseAuthUser(
    val id: String,
    val email: String?,
    val displayName: String? = null,
    val username: String? = null,
)

object EmptySupabaseAuthSessionClient : SupabaseAuthSessionClient {
    override fun currentUserOrNull(): SupabaseAuthUser? = null
    override fun signOut() {}
}

class SupabaseAuthRemoteDataSource(
    private val sessionClient: SupabaseAuthSessionClient = EmptySupabaseAuthSessionClient,
) : AuthRemoteDataSource {
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
        return sessionClient.currentUserOrNull()?.toDomainUser()
    }

    override fun signOut() {
        if (sessionClient.currentUserOrNull() != null) {
            sessionClient.signOut()
        }
    }

    private fun SupabaseAuthUser.toDomainUser(): User {
        val fallbackName = email?.substringBefore("@").orEmpty()
        val resolvedDisplayName = displayName ?: fallbackName

        return User(
            id = id,
            displayName = resolvedDisplayName,
            username = username ?: resolvedDisplayName,
            email = email.orEmpty(),
            about = "",
            website = "",
        )
    }
}
