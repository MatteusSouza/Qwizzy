package com.example.askceny.data.repositories

import com.example.askceny.data.remote.api.SupabaseAuthFailureException
import com.example.askceny.data.remote.api.SupabaseAuthRemoteDataSource
import com.example.askceny.data.remote.api.SupabaseAuthSessionClient
import com.example.askceny.data.remote.api.SupabaseAuthSuccess
import com.example.askceny.data.remote.api.SupabaseAuthUser
import com.example.askceny.domain.types.AuthState
import com.example.askceny.domain.types.ErrorCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SupabaseAuthRuntimeRepositoryTest {
    @Test
    fun `runtime email sign-in returns authenticated when Supabase creates a session`() = runTest {
        val client = ScriptedSupabaseAuthSessionClient(
            signInWithEmailResult = SupabaseAuthSuccess.Authenticated(sampleSupabaseUser)
        )
        val repository = AuthRepositoryImpl(SupabaseAuthRemoteDataSource(client))

        val result = repository.signInWithEmail(
            email = "ada@example.com",
            password = "correct-horse-battery-staple"
        )

        assertEquals(AuthState.Authenticated, result)
        assertEquals(
            EmailSignInRequest(
                email = "ada@example.com",
                password = "correct-horse-battery-staple"
            ),
            client.emailSignInRequest
        )
    }

    @Test
    fun `runtime email sign-in maps Supabase invalid credentials to auth error`() = runTest {
        val client = ScriptedSupabaseAuthSessionClient(
            signInWithEmailError = SupabaseAuthFailureException("invalid_credentials")
        )
        val repository = AuthRepositoryImpl(SupabaseAuthRemoteDataSource(client))

        val result = repository.signInWithEmail(
            email = "ada@example.com",
            password = "wrong-password"
        )

        val error = assertIs<AuthState.AuthError>(result)
        assertEquals(ErrorCode.INVALID_CREDENTIALS, error.errorCode)
    }

    private data class EmailSignInRequest(
        val email: String,
        val password: String,
    )

    private class ScriptedSupabaseAuthSessionClient(
        private val signInWithEmailResult: SupabaseAuthSuccess =
            SupabaseAuthSuccess.Authenticated(sampleSupabaseUser),
        private val signInWithEmailError: SupabaseAuthFailureException? = null,
    ) : SupabaseAuthSessionClient {
        var emailSignInRequest: EmailSignInRequest? = null
            private set

        override fun currentUserOrNull(): SupabaseAuthUser? = null

        override fun signOut() {}

        override suspend fun signInWithEmail(email: String, password: String): SupabaseAuthSuccess {
            emailSignInRequest = EmailSignInRequest(email, password)
            signInWithEmailError?.let { throw it }
            return signInWithEmailResult
        }
    }

    private companion object {
        val sampleSupabaseUser = SupabaseAuthUser(
            id = "user-id",
            email = "ada@example.com",
            displayName = "Ada Lovelace",
            username = "adalovelace",
        )
    }
}
