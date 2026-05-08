package com.example.askceny.data.remote.api

import com.example.askceny.domain.models.User
import com.example.askceny.domain.types.ErrorCode
import kotlinx.coroutines.CancellationException

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
    val isPlaceholder: Boolean
    fun currentUserOrNull(): SupabaseAuthUser?
    fun signOut()
    suspend fun signInWithEmail(email: String, password: String): SupabaseAuthSuccess
    suspend fun signUpWithEmail(displayName: String, email: String, password: String): SupabaseAuthSuccess
    suspend fun signInWithGoogle(idToken: String, nonce: String?): SupabaseAuthSuccess
    suspend fun signUpWithGoogle(idToken: String, nonce: String?): SupabaseAuthSuccess
}

data class SupabaseAuthUser(
    val id: String,
    val email: String?,
    val displayName: String? = null,
    val username: String? = null,
)

object EmptySupabaseAuthSessionClient : SupabaseAuthSessionClient {
    override val isPlaceholder: Boolean = true

    override fun currentUserOrNull(): SupabaseAuthUser? = null
    override fun signOut() {}

    override suspend fun signInWithEmail(email: String, password: String): SupabaseAuthSuccess {
        throw SupabaseAuthFailureException(ErrorCode.UNEXPECTED_FAILURE.supabaseCode)
    }

    override suspend fun signUpWithEmail(
        displayName: String,
        email: String,
        password: String,
    ): SupabaseAuthSuccess {
        throw SupabaseAuthFailureException(ErrorCode.UNEXPECTED_FAILURE.supabaseCode)
    }

    override suspend fun signInWithGoogle(idToken: String, nonce: String?): SupabaseAuthSuccess {
        throw SupabaseAuthFailureException(ErrorCode.UNEXPECTED_FAILURE.supabaseCode)
    }

    override suspend fun signUpWithGoogle(idToken: String, nonce: String?): SupabaseAuthSuccess {
        throw SupabaseAuthFailureException(ErrorCode.UNEXPECTED_FAILURE.supabaseCode)
    }
}

sealed class SupabaseAuthSuccess {
    data class Authenticated(val user: SupabaseAuthUser? = null): SupabaseAuthSuccess()
    data class EmailConfirmationRequired(val user: SupabaseAuthUser? = null): SupabaseAuthSuccess()
}

class SupabaseAuthFailureException(
    val supabaseCode: String?,
    cause: Throwable? = null,
) : Exception(supabaseCode, cause)

class SupabaseAuthRemoteDataSource(
    private val sessionClient: SupabaseAuthSessionClient = SupabaseAuthSessionClientFactory.create(),
) : AuthRemoteDataSource {
    internal val isUsingPlaceholderClient: Boolean
        get() = sessionClient.isPlaceholder

    override suspend fun signUpWithEmail(displayName: String, email: String, password: String): AuthRemoteResult {
        return runAuthCall {
            sessionClient.signUpWithEmail(displayName, email, password)
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): AuthRemoteResult {
        return runAuthCall {
            sessionClient.signInWithEmail(email, password)
        }
    }

    override suspend fun signUpWithGoogle(idToken: String, nonce: String?): AuthRemoteResult {
        return runAuthCall {
            sessionClient.signUpWithGoogle(idToken, nonce)
        }
    }

    override suspend fun signInWithGoogle(idToken: String, nonce: String?): AuthRemoteResult {
        return runAuthCall {
            sessionClient.signInWithGoogle(idToken, nonce)
        }
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

    private suspend fun runAuthCall(block: suspend () -> SupabaseAuthSuccess): AuthRemoteResult {
        return try {
            block().toRemoteResult()
        } catch (e: CancellationException) {
            throw e
        } catch (e: SupabaseAuthFailureException) {
            AuthRemoteResult.Failure(ErrorCode.fromSupabaseCode(e.supabaseCode))
        } catch (e: Exception) {
            AuthRemoteResult.Failure(ErrorCode.UNEXPECTED_FAILURE)
        }
    }

    private fun SupabaseAuthSuccess.toRemoteResult(): AuthRemoteResult {
        return when (this) {
            is SupabaseAuthSuccess.Authenticated -> AuthRemoteResult.Authenticated(user?.toDomainUser())
            is SupabaseAuthSuccess.EmailConfirmationRequired -> {
                AuthRemoteResult.EmailConfirmationRequired(user?.toDomainUser())
            }
        }
    }
}
