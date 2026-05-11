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

    @Test
    fun `runtime email sign-up returns confirmation required when Supabase requires email confirmation`() = runTest {
        val client = ScriptedSupabaseAuthSessionClient(
            signUpWithEmailResult = SupabaseAuthSuccess.EmailConfirmationRequired(sampleSupabaseUser)
        )
        val repository = AuthRepositoryImpl(SupabaseAuthRemoteDataSource(client))

        val result = repository.signUpWithEmail(
            displayName = "Ada Lovelace",
            email = "ada@example.com",
            password = "correct-horse-battery-staple"
        )

        assertEquals(AuthState.EmailConfirmationRequired("ada@example.com"), result)
        assertEquals(
            EmailSignUpRequest(
                displayName = "Ada Lovelace",
                email = "ada@example.com",
                password = "correct-horse-battery-staple"
            ),
            client.emailSignUpRequest
        )
    }

    @Test
    fun `runtime email sign-up returns authenticated when Supabase creates an immediate session`() = runTest {
        val client = ScriptedSupabaseAuthSessionClient(
            signUpWithEmailResult = SupabaseAuthSuccess.Authenticated(sampleSupabaseUser)
        )
        val repository = AuthRepositoryImpl(SupabaseAuthRemoteDataSource(client))

        val result = repository.signUpWithEmail(
            displayName = "Ada Lovelace",
            email = "ada@example.com",
            password = "correct-horse-battery-staple"
        )

        assertEquals(AuthState.Authenticated, result)
    }

    @Test
    fun `runtime email sign-up maps Supabase existing user failure to auth error`() = runTest {
        val client = ScriptedSupabaseAuthSessionClient(
            signUpWithEmailError = SupabaseAuthFailureException("user_already_exists")
        )
        val repository = AuthRepositoryImpl(SupabaseAuthRemoteDataSource(client))

        val result = repository.signUpWithEmail(
            displayName = "Ada Lovelace",
            email = "ada@example.com",
            password = "correct-horse-battery-staple"
        )

        val error = assertIs<AuthState.AuthError>(result)
        assertEquals(ErrorCode.USER_ALREADY_EXISTS, error.errorCode)
    }

    @Test
    fun `runtime google sign-in passes ID token and nonce to Supabase`() = runTest {
        val client = ScriptedSupabaseAuthSessionClient(
            signInWithGoogleResult = SupabaseAuthSuccess.Authenticated(sampleSupabaseUser)
        )
        val repository = AuthRepositoryImpl(SupabaseAuthRemoteDataSource(client))

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
            client.googleSignInRequest
        )
    }

    @Test
    fun `runtime google sign-in maps Supabase authorization failure to auth error`() = runTest {
        val client = ScriptedSupabaseAuthSessionClient(
            signInWithGoogleError = SupabaseAuthFailureException("no_authorization")
        )
        val repository = AuthRepositoryImpl(SupabaseAuthRemoteDataSource(client))

        val result = repository.signInWithGoogle(
            idToken = "invalid-google-id-token",
            nonce = "raw-nonce"
        )

        val error = assertIs<AuthState.AuthError>(result)
        assertEquals(ErrorCode.NO_AUTHORIZATION, error.errorCode)
    }

    @Test
    fun `runtime google sign-up uses the same Supabase ID token flow`() = runTest {
        val client = ScriptedSupabaseAuthSessionClient(
            signUpWithGoogleResult = SupabaseAuthSuccess.Authenticated(sampleSupabaseUser)
        )
        val repository = AuthRepositoryImpl(SupabaseAuthRemoteDataSource(client))

        val result = repository.signUpWithGoogle(
            idToken = "google-id-token",
            nonce = null
        )

        assertEquals(AuthState.Authenticated, result)
        assertEquals(
            GoogleAuthRequest(
                idToken = "google-id-token",
                nonce = null
            ),
            client.googleSignUpRequest
        )
    }

    @Test
    fun `runtime email otp verification passes email and token to Supabase`() = runTest {
        val client = ScriptedSupabaseAuthSessionClient(
            verifyEmailOtpResult = SupabaseAuthSuccess.Authenticated(sampleSupabaseUser)
        )
        val repository = AuthRepositoryImpl(SupabaseAuthRemoteDataSource(client))

        val result = repository.verifyEmailOtp(
            email = "ada@example.com",
            token = "123456"
        )

        assertEquals(AuthState.Authenticated, result)
        assertEquals(
            EmailOtpRequest(
                email = "ada@example.com",
                token = "123456"
            ),
            client.verifyEmailOtpRequest
        )
    }

    @Test
    fun `runtime email otp verification maps verified no session to controlled auth error`() = runTest {
        val client = ScriptedSupabaseAuthSessionClient(
            verifyEmailOtpResult = SupabaseAuthSuccess.VerifiedNoSession
        )
        val repository = AuthRepositoryImpl(SupabaseAuthRemoteDataSource(client))

        val result = repository.verifyEmailOtp(
            email = "ada@example.com",
            token = "123456"
        )

        val error = assertIs<AuthState.AuthError>(result)
        assertEquals(ErrorCode.SESSION_NOT_FOUND, error.errorCode)
    }

    @Test
    fun `runtime email otp verification maps invalid or expired token failure`() = runTest {
        val client = ScriptedSupabaseAuthSessionClient(
            verifyEmailOtpError = SupabaseAuthFailureException("validation_failed")
        )
        val repository = AuthRepositoryImpl(SupabaseAuthRemoteDataSource(client))

        val result = repository.verifyEmailOtp(
            email = "ada@example.com",
            token = "000000"
        )

        val error = assertIs<AuthState.AuthError>(result)
        assertEquals(ErrorCode.VALIDATION_FAILED, error.errorCode)
    }

    @Test
    fun `runtime email otp verification preserves Supabase expired otp code`() = runTest {
        val client = ScriptedSupabaseAuthSessionClient(
            verifyEmailOtpError = SupabaseAuthFailureException("otp_expired")
        )
        val repository = AuthRepositoryImpl(SupabaseAuthRemoteDataSource(client))

        val result = repository.verifyEmailOtp(
            email = "ada@example.com",
            token = "000000"
        )

        val error = assertIs<AuthState.AuthError>(result)
        assertEquals(ErrorCode.OTP_EXPIRED, error.errorCode)
    }

    @Test
    fun `runtime email otp verification preserves known code from generic exception message`() = runTest {
        val client = ScriptedSupabaseAuthSessionClient(
            verifyEmailOtpError = IllegalStateException("AuthRestException: otp_expired: token has expired")
        )
        val repository = AuthRepositoryImpl(SupabaseAuthRemoteDataSource(client))

        val result = repository.verifyEmailOtp(
            email = "ada@example.com",
            token = "000000"
        )

        val error = assertIs<AuthState.AuthError>(result)
        assertEquals(ErrorCode.OTP_EXPIRED, error.errorCode)
    }

    @Test
    fun `runtime resend sign-up email otp passes email to Supabase`() = runTest {
        val client = ScriptedSupabaseAuthSessionClient(
            resendSignUpEmailOtpResult = SupabaseAuthSuccess.EmailConfirmationRequired()
        )
        val repository = AuthRepositoryImpl(SupabaseAuthRemoteDataSource(client))

        val result = repository.resendSignUpEmailOtp(email = "ada@example.com")

        assertEquals(AuthState.EmailConfirmationRequired("ada@example.com"), result)
        assertEquals("ada@example.com", client.resendSignUpEmailOtpRequest)
    }

    @Test
    fun `runtime resend sign-up email otp maps rate limit failure`() = runTest {
        val client = ScriptedSupabaseAuthSessionClient(
            resendSignUpEmailOtpError = SupabaseAuthFailureException("over_request_rate_limit")
        )
        val repository = AuthRepositoryImpl(SupabaseAuthRemoteDataSource(client))

        val result = repository.resendSignUpEmailOtp(email = "ada@example.com")

        val error = assertIs<AuthState.AuthError>(result)
        assertEquals(ErrorCode.OVER_REQUEST_RATE_LIMIT, error.errorCode)
    }

    @Test
    fun `runtime resend sign-up email otp maps email send rate limit failure`() = runTest {
        val client = ScriptedSupabaseAuthSessionClient(
            resendSignUpEmailOtpError = SupabaseAuthFailureException("over_email_send_rate_limit")
        )
        val repository = AuthRepositoryImpl(SupabaseAuthRemoteDataSource(client))

        val result = repository.resendSignUpEmailOtp(email = "ada@example.com")

        val error = assertIs<AuthState.AuthError>(result)
        assertEquals(ErrorCode.OVER_EMAIL_SEND_RATE_LIMIT, error.errorCode)
    }


    private data class EmailSignInRequest(
        val email: String,
        val password: String,
    )

    private data class EmailSignUpRequest(
        val displayName: String,
        val email: String,
        val password: String,
    )

    private data class GoogleAuthRequest(
        val idToken: String,
        val nonce: String?,
    )

    private data class EmailOtpRequest(
        val email: String,
        val token: String,
    )

    private class ScriptedSupabaseAuthSessionClient(
        private val signInWithEmailResult: SupabaseAuthSuccess =
            SupabaseAuthSuccess.Authenticated(sampleSupabaseUser),
        private val signInWithEmailError: SupabaseAuthFailureException? = null,
        private val signUpWithEmailResult: SupabaseAuthSuccess =
            SupabaseAuthSuccess.EmailConfirmationRequired(sampleSupabaseUser),
        private val signUpWithEmailError: SupabaseAuthFailureException? = null,
        private val signInWithGoogleResult: SupabaseAuthSuccess =
            SupabaseAuthSuccess.Authenticated(sampleSupabaseUser),
        private val signInWithGoogleError: SupabaseAuthFailureException? = null,
        private val signUpWithGoogleResult: SupabaseAuthSuccess =
            SupabaseAuthSuccess.Authenticated(sampleSupabaseUser),
        private val signUpWithGoogleError: SupabaseAuthFailureException? = null,
        private val verifyEmailOtpResult: SupabaseAuthSuccess =
            SupabaseAuthSuccess.Authenticated(sampleSupabaseUser),
        private val verifyEmailOtpError: Exception? = null,
        private val resendSignUpEmailOtpResult: SupabaseAuthSuccess =
            SupabaseAuthSuccess.EmailConfirmationRequired(),
        private val resendSignUpEmailOtpError: Exception? = null,
    ) : SupabaseAuthSessionClient {
        override val isPlaceholder: Boolean = false

        var emailSignInRequest: EmailSignInRequest? = null
            private set
        var emailSignUpRequest: EmailSignUpRequest? = null
            private set
        var googleSignInRequest: GoogleAuthRequest? = null
            private set
        var googleSignUpRequest: GoogleAuthRequest? = null
            private set
        var verifyEmailOtpRequest: EmailOtpRequest? = null
            private set
        var resendSignUpEmailOtpRequest: String? = null
            private set

        override fun currentUserOrNull(): SupabaseAuthUser? = null

        override fun signOut() {}

        override suspend fun signInWithEmail(email: String, password: String): SupabaseAuthSuccess {
            emailSignInRequest = EmailSignInRequest(email, password)
            signInWithEmailError?.let { throw it }
            return signInWithEmailResult
        }

        override suspend fun signUpWithEmail(
            displayName: String,
            email: String,
            password: String,
        ): SupabaseAuthSuccess {
            emailSignUpRequest = EmailSignUpRequest(displayName, email, password)
            signUpWithEmailError?.let { throw it }
            return signUpWithEmailResult
        }

        override suspend fun signInWithGoogle(idToken: String, nonce: String?): SupabaseAuthSuccess {
            googleSignInRequest = GoogleAuthRequest(idToken, nonce)
            signInWithGoogleError?.let { throw it }
            return signInWithGoogleResult
        }

        override suspend fun signUpWithGoogle(idToken: String, nonce: String?): SupabaseAuthSuccess {
            googleSignUpRequest = GoogleAuthRequest(idToken, nonce)
            signUpWithGoogleError?.let { throw it }
            return signUpWithGoogleResult
        }

        override suspend fun verifyEmailOtp(email: String, token: String): SupabaseAuthSuccess {
            verifyEmailOtpRequest = EmailOtpRequest(email, token)
            verifyEmailOtpError?.let { throw it }
            return verifyEmailOtpResult
        }

        override suspend fun resendSignUpEmailOtp(email: String): SupabaseAuthSuccess {
            resendSignUpEmailOtpRequest = email
            resendSignUpEmailOtpError?.let { throw it }
            return resendSignUpEmailOtpResult
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
