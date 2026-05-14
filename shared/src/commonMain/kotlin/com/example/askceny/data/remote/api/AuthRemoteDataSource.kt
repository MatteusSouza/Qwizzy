package com.example.askceny.data.remote.api

import com.example.askceny.domain.models.User
import com.example.askceny.domain.types.AuthState
import com.example.askceny.domain.types.ErrorCode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

interface AuthRemoteDataSource {
    suspend fun signUpWithEmail(displayName: String, email: String, password: String): AuthRemoteResult
    suspend fun signInWithEmail(email: String, password: String): AuthRemoteResult
    suspend fun signUpWithGoogle(idToken: String, nonce: String? = null): AuthRemoteResult
    suspend fun signInWithGoogle(idToken: String, nonce: String? = null): AuthRemoteResult
    suspend fun verifyEmailOtp(email: String, token: String): AuthRemoteResult
    suspend fun resendSignUpEmailOtp(email: String): AuthRemoteResult
    fun observeAuthState(): Flow<AuthState>
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
    fun observeSessionStatus(): Flow<SupabaseAuthSessionStatus>
    fun signOut()
    suspend fun signInWithEmail(email: String, password: String): SupabaseAuthSuccess
    suspend fun signUpWithEmail(displayName: String, email: String, password: String): SupabaseAuthSuccess
    suspend fun signInWithGoogle(idToken: String, nonce: String?): SupabaseAuthSuccess
    suspend fun signUpWithGoogle(idToken: String, nonce: String?): SupabaseAuthSuccess
    suspend fun verifyEmailOtp(email: String, token: String): SupabaseAuthSuccess
    suspend fun resendSignUpEmailOtp(email: String): SupabaseAuthSuccess
}

data class SupabaseAuthUser(
    val id: String,
    val email: String?,
    val displayName: String? = null,
    val username: String? = null,
    val identityCount: Int? = null,
)

sealed class SupabaseAuthSessionStatus {
    object Loading: SupabaseAuthSessionStatus()
    data class Authenticated(val user: SupabaseAuthUser? = null): SupabaseAuthSessionStatus()
    object Unauthenticated: SupabaseAuthSessionStatus()
    object NetworkError: SupabaseAuthSessionStatus()
}

object EmptySupabaseAuthSessionClient : SupabaseAuthSessionClient {
    override val isPlaceholder: Boolean = true

    override fun currentUserOrNull(): SupabaseAuthUser? = null
    override fun observeSessionStatus(): Flow<SupabaseAuthSessionStatus> =
        flowOf(SupabaseAuthSessionStatus.Unauthenticated)

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

    override suspend fun verifyEmailOtp(email: String, token: String): SupabaseAuthSuccess {
        throw SupabaseAuthFailureException(ErrorCode.UNEXPECTED_FAILURE.supabaseCode)
    }

    override suspend fun resendSignUpEmailOtp(email: String): SupabaseAuthSuccess {
        throw SupabaseAuthFailureException(ErrorCode.UNEXPECTED_FAILURE.supabaseCode)
    }
}

sealed class SupabaseAuthSuccess {
    data class Authenticated(val user: SupabaseAuthUser? = null): SupabaseAuthSuccess()
    data class EmailConfirmationRequired(val user: SupabaseAuthUser? = null): SupabaseAuthSuccess()
    object VerifiedNoSession: SupabaseAuthSuccess()
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
        return try {
            sessionClient.signUpWithEmail(displayName, email, password).toSignUpRemoteResult(email)
        } catch (e: CancellationException) {
            throw e
        } catch (e: SupabaseAuthFailureException) {
            AuthRemoteResult.Failure(ErrorCode.fromSupabaseCode(e.supabaseCode))
        } catch (e: Exception) {
            AuthRemoteResult.Failure(ErrorCode.fromSupabaseCode(e.message))
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

    override suspend fun verifyEmailOtp(email: String, token: String): AuthRemoteResult {
        return runAuthCall {
            sessionClient.verifyEmailOtp(email, token)
        }
    }

    override suspend fun resendSignUpEmailOtp(email: String): AuthRemoteResult {
        return runAuthCall {
            sessionClient.resendSignUpEmailOtp(email)
        }
    }

    override fun observeAuthState(): Flow<AuthState> {
        return sessionClient.observeSessionStatus()
            .map { status -> status.toAuthState() }
            .catch { e ->
                if (e is CancellationException) throw e
                emit(AuthState.AuthError(ErrorCode.UNEXPECTED_FAILURE))
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

    private fun SupabaseAuthSessionStatus.toAuthState(): AuthState {
        return when (this) {
            SupabaseAuthSessionStatus.Loading -> AuthState.Loading
            is SupabaseAuthSessionStatus.Authenticated -> AuthState.Authenticated
            SupabaseAuthSessionStatus.Unauthenticated -> AuthState.Unauthenticated
            SupabaseAuthSessionStatus.NetworkError -> AuthState.AuthError(ErrorCode.NETWORK_ERROR)
        }
    }

    private suspend fun runAuthCall(block: suspend () -> SupabaseAuthSuccess): AuthRemoteResult {
        return try {
            block().toRemoteResult()
        } catch (e: CancellationException) {
            throw e
        } catch (e: SupabaseAuthFailureException) {
            AuthRemoteResult.Failure(ErrorCode.fromSupabaseCode(e.supabaseCode))
        } catch (e: Exception) {
            AuthRemoteResult.Failure(ErrorCode.fromSupabaseCode(e.message))
        }
    }

    private fun SupabaseAuthSuccess.toRemoteResult(): AuthRemoteResult {
        return when (this) {
            is SupabaseAuthSuccess.Authenticated -> AuthRemoteResult.Authenticated(user?.toDomainUser())
            is SupabaseAuthSuccess.EmailConfirmationRequired -> {
                AuthRemoteResult.EmailConfirmationRequired(user?.toDomainUser())
            }
            SupabaseAuthSuccess.VerifiedNoSession -> AuthRemoteResult.Failure(ErrorCode.SESSION_NOT_FOUND)
        }
    }

    private fun SupabaseAuthSuccess.toSignUpRemoteResult(requestedEmail: String): AuthRemoteResult {
        if (this is SupabaseAuthSuccess.EmailConfirmationRequired && user.isObfuscatedRepeatedSignUp(requestedEmail)) {
            return AuthRemoteResult.Failure(ErrorCode.USER_ALREADY_EXISTS)
        }

        return toRemoteResult()
    }

    private fun SupabaseAuthUser?.isObfuscatedRepeatedSignUp(requestedEmail: String): Boolean {
        val normalizedReturnedEmail = this?.email?.trim().orEmpty()
        val normalizedRequestedEmail = requestedEmail.trim()

        return normalizedReturnedEmail.isEmpty() ||
            !normalizedReturnedEmail.equals(normalizedRequestedEmail, ignoreCase = true) ||
            this?.identityCount == 0
    }
}
