package com.example.askceny.data.repositories

import com.example.askceny.data.remote.api.AuthRemoteDataSource
import com.example.askceny.data.remote.api.AuthRemoteResult
import com.example.askceny.domain.models.User
import com.example.askceny.domain.types.AuthState
import com.example.askceny.domain.types.ErrorCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AuthRepositoryImplTest {
    @Test
    fun `email sign-up returns confirmation required when Supabase returns a pending confirmed-email user`() = runTest {
        val remoteDataSource = RecordingAuthRemoteDataSource(
            signUpWithEmailResult = AuthRemoteResult.EmailConfirmationRequired(sampleUser)
        )
        val repository = AuthRepositoryImpl(remoteDataSource)

        val result = repository.signUpWithEmail(
            displayName = "Ada Lovelace",
            email = "ada@example.com",
            password = "correct-horse-battery-staple"
        )

        assertEquals(AuthState.EmailConfirmationRequired, result)
        assertEquals(
            EmailSignUpRequest(
                displayName = "Ada Lovelace",
                email = "ada@example.com",
                password = "correct-horse-battery-staple"
            ),
            remoteDataSource.emailSignUpRequest
        )
    }

    @Test
    fun `email sign-up returns authenticated when Supabase creates an immediate session`() = runTest {
        val remoteDataSource = RecordingAuthRemoteDataSource(
            signUpWithEmailResult = AuthRemoteResult.Authenticated(sampleUser)
        )
        val repository = AuthRepositoryImpl(remoteDataSource)

        val result = repository.signUpWithEmail(
            displayName = "Ada Lovelace",
            email = "ada@example.com",
            password = "correct-horse-battery-staple"
        )

        assertEquals(AuthState.Authenticated, result)
    }

    @Test
    fun `legacy email sign-up method uses Supabase email sign-up contract`() = runTest {
        val remoteDataSource = RecordingAuthRemoteDataSource(
            signUpWithEmailResult = AuthRemoteResult.EmailConfirmationRequired(sampleUser)
        )
        val repository = AuthRepositoryImpl(remoteDataSource)

        val result = repository.createUserWithEmailAndPassword(
            displayName = "Ada Lovelace",
            email = "ada@example.com",
            password = "correct-horse-battery-staple"
        )

        assertEquals(AuthState.EmailConfirmationRequired, result)
        assertEquals("ada@example.com", remoteDataSource.emailSignUpRequest?.email)
    }

    @Test
    fun `email sign-in returns authenticated when Supabase creates a session`() = runTest {
        val remoteDataSource = RecordingAuthRemoteDataSource(
            signInWithEmailResult = AuthRemoteResult.Authenticated(sampleUser)
        )
        val repository = AuthRepositoryImpl(remoteDataSource)

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
            remoteDataSource.emailSignInRequest
        )
    }

    @Test
    fun `legacy email sign-in method uses Supabase email sign-in contract`() = runTest {
        val remoteDataSource = RecordingAuthRemoteDataSource(
            signInWithEmailResult = AuthRemoteResult.Authenticated(sampleUser)
        )
        val repository = AuthRepositoryImpl(remoteDataSource)

        val result = repository.signInWithEmailAndPassword(
            email = "ada@example.com",
            password = "correct-horse-battery-staple"
        )

        assertEquals(AuthState.Authenticated, result)
        assertEquals("ada@example.com", remoteDataSource.emailSignInRequest?.email)
    }

    @Test
    fun `email sign-in maps Supabase auth failures to AuthError`() = runTest {
        val remoteDataSource = RecordingAuthRemoteDataSource(
            signInWithEmailResult = AuthRemoteResult.Failure(ErrorCode.INVALID_CREDENTIALS)
        )
        val repository = AuthRepositoryImpl(remoteDataSource)

        val result = repository.signInWithEmail(
            email = "ada@example.com",
            password = "wrong-password"
        )

        val error = assertIs<AuthState.AuthError>(result)
        assertEquals(ErrorCode.INVALID_CREDENTIALS, error.errorCode)
    }

    private data class EmailSignUpRequest(
        val displayName: String,
        val email: String,
        val password: String,
    )

    private data class EmailSignInRequest(
        val email: String,
        val password: String,
    )

    private data class GoogleAuthRequest(
        val idToken: String,
        val nonce: String?,
    )

    private class RecordingAuthRemoteDataSource(
        private val signUpWithEmailResult: AuthRemoteResult = AuthRemoteResult.Authenticated(sampleUser),
        private val signInWithEmailResult: AuthRemoteResult = AuthRemoteResult.Authenticated(sampleUser),
        private val signUpWithGoogleResult: AuthRemoteResult = AuthRemoteResult.Authenticated(sampleUser),
        private val signInWithGoogleResult: AuthRemoteResult = AuthRemoteResult.Authenticated(sampleUser),
    ) : AuthRemoteDataSource {
        var emailSignUpRequest: EmailSignUpRequest? = null
            private set
        var emailSignInRequest: EmailSignInRequest? = null
            private set
        var googleSignUpRequest: GoogleAuthRequest? = null
            private set
        var googleSignInRequest: GoogleAuthRequest? = null
            private set
        var signOutCalls = 0
            private set

        override suspend fun signUpWithEmail(displayName: String, email: String, password: String): AuthRemoteResult {
            emailSignUpRequest = EmailSignUpRequest(displayName, email, password)
            return signUpWithEmailResult
        }

        override suspend fun signInWithEmail(email: String, password: String): AuthRemoteResult {
            emailSignInRequest = EmailSignInRequest(email, password)
            return signInWithEmailResult
        }

        override suspend fun signUpWithGoogle(idToken: String, nonce: String?): AuthRemoteResult {
            googleSignUpRequest = GoogleAuthRequest(idToken, nonce)
            return signUpWithGoogleResult
        }

        override suspend fun signInWithGoogle(idToken: String, nonce: String?): AuthRemoteResult {
            googleSignInRequest = GoogleAuthRequest(idToken, nonce)
            return signInWithGoogleResult
        }

        override fun getCurrentUser(): User? = sampleUser

        override fun signOut() {
            signOutCalls += 1
        }
    }

    private companion object {
        val sampleUser = User(
            id = "user-id",
            displayName = "Ada Lovelace",
            username = "adalovelace",
            email = "ada@example.com",
            about = "",
            website = "",
        )
    }
}
