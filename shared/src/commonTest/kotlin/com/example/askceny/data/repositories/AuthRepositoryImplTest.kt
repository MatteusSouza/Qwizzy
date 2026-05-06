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
import kotlin.test.assertNull
import kotlin.test.assertTrue

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

    @Test
    fun `google sign-in passes ID token and nonce to Supabase auth`() = runTest {
        val remoteDataSource = RecordingAuthRemoteDataSource(
            signInWithGoogleResult = AuthRemoteResult.Authenticated(sampleUser)
        )
        val repository = AuthRepositoryImpl(remoteDataSource)

        val result = repository.signInWithGoogle(
            idToken = "google-id-token",
            nonce = "raw-nonce"
        )

        assertEquals(AuthState.Authenticated, result)
        assertEquals(
            GoogleAuthRequest(
                idToken = "google-id-token",
                nonce = "raw-nonce"
            ),
            remoteDataSource.googleSignInRequest
        )
    }

    @Test
    fun `google sign-in allows missing nonce for flows that do not provide one`() = runTest {
        val remoteDataSource = RecordingAuthRemoteDataSource(
            signInWithGoogleResult = AuthRemoteResult.Authenticated(sampleUser)
        )
        val repository = AuthRepositoryImpl(remoteDataSource)

        val result = repository.signInWithGoogle(idToken = "google-id-token")

        assertEquals(AuthState.Authenticated, result)
        assertEquals(
            GoogleAuthRequest(
                idToken = "google-id-token",
                nonce = null
            ),
            remoteDataSource.googleSignInRequest
        )
    }

    @Test
    fun `google sign-up uses the same Supabase ID token contract`() = runTest {
        val remoteDataSource = RecordingAuthRemoteDataSource(
            signUpWithGoogleResult = AuthRemoteResult.Authenticated(sampleUser)
        )
        val repository = AuthRepositoryImpl(remoteDataSource)

        val result = repository.signUpWithGoogle(
            idToken = "google-id-token",
            nonce = "raw-nonce"
        )

        assertEquals(AuthState.Authenticated, result)
        assertEquals(
            GoogleAuthRequest(
                idToken = "google-id-token",
                nonce = "raw-nonce"
            ),
            remoteDataSource.googleSignUpRequest
        )
    }

    @Test
    fun `google auth maps Supabase auth failures to AuthError`() = runTest {
        val remoteDataSource = RecordingAuthRemoteDataSource(
            signInWithGoogleResult = AuthRemoteResult.Failure(ErrorCode.NO_AUTHORIZATION)
        )
        val repository = AuthRepositoryImpl(remoteDataSource)

        val result = repository.signInWithGoogle(
            idToken = "invalid-google-id-token",
            nonce = "raw-nonce"
        )

        val error = assertIs<AuthState.AuthError>(result)
        assertEquals(ErrorCode.NO_AUTHORIZATION, error.errorCode)
    }

    @Test
    fun `current user returns user when Supabase session is authenticated`() {
        val remoteDataSource = RecordingAuthRemoteDataSource(currentUser = sampleUser)
        val repository = AuthRepositoryImpl(remoteDataSource)

        val result = repository.getCurrentUser()

        assertEquals(sampleUser, result)
    }

    @Test
    fun `current user returns null when Supabase has no authenticated session`() {
        val remoteDataSource = RecordingAuthRemoteDataSource(currentUser = null)
        val repository = AuthRepositoryImpl(remoteDataSource)

        val result = repository.getCurrentUser()

        assertNull(result)
    }

    @Test
    fun `current user lookup does not crash on cold start without a Supabase session`() {
        val remoteDataSource = RecordingAuthRemoteDataSource(currentUser = null)
        val repository = AuthRepositoryImpl(remoteDataSource)

        val result = runCatching { repository.getCurrentUser() }

        assertTrue(result.isSuccess)
        assertNull(result.getOrThrow())
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
        private val currentUser: User? = sampleUser,
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

        override fun getCurrentUser(): User? = currentUser

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
