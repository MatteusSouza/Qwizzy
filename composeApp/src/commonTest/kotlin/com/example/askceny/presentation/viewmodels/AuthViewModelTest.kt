package com.example.askceny.presentation.viewmodels

import com.example.askceny.domain.models.User
import com.example.askceny.domain.repositories.AuthRepository
import com.example.askceny.domain.types.AuthState
import com.example.askceny.domain.types.ErrorCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {
    @Test
    fun `auth bootstrap failure does not terminate app flow`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        try {
            val result = runCatching {
                AuthViewModel(FailingBootstrapAuthRepository()).also {
                    advanceUntilIdle()
                }
            }

            assertTrue(result.isSuccess)
            assertEquals(AuthState.Unauthenticated, result.getOrThrow().authState.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `auth bootstrap failure transitions from loading to unauthenticated`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        try {
            val viewModel = AuthViewModel(FailingBootstrapAuthRepository())

            assertEquals(AuthState.Loading, viewModel.authState.value)
            advanceUntilIdle()
            assertEquals(AuthState.Unauthenticated, viewModel.authState.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `auth bootstrap restores authenticated session from observed state`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        try {
            val viewModel = AuthViewModel(
                RecordingAuthRepository(
                    observedAuthStates = listOf(AuthState.Loading, AuthState.Authenticated)
                )
            )

            assertEquals(AuthState.Loading, viewModel.authState.value)
            advanceUntilIdle()
            assertEquals(AuthState.Authenticated, viewModel.authState.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `auth bootstrap resolves unauthenticated when observed state has no session`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        try {
            val viewModel = AuthViewModel(
                RecordingAuthRepository(
                    observedAuthStates = listOf(AuthState.Loading, AuthState.Unauthenticated)
                )
            )

            assertEquals(AuthState.Loading, viewModel.authState.value)
            advanceUntilIdle()
            assertEquals(AuthState.Unauthenticated, viewModel.authState.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `sign-in rejects malformed email before repository call`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        try {
            val repository = RecordingAuthRepository()
            val viewModel = AuthViewModel(repository)
            advanceUntilIdle()

            viewModel.signIn("not-an-email", "password")
            advanceUntilIdle()

            assertEquals(0, repository.signInCalls)
            assertEquals("Invalid email", viewModel.emailError.value)
            assertEquals(AuthState.Unauthenticated, viewModel.authState.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `sign-up rejects malformed email before repository call`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        try {
            val repository = RecordingAuthRepository()
            val viewModel = AuthViewModel(repository)
            advanceUntilIdle()

            viewModel.signUp("User", "not-an-email", "password")
            advanceUntilIdle()

            assertEquals(0, repository.signUpCalls)
            assertEquals("Invalid email", viewModel.emailError.value)
            assertEquals(AuthState.Unauthenticated, viewModel.authState.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `invalid credentials show visible email and password errors`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        try {
            val viewModel = AuthViewModel(
                RecordingAuthRepository(
                    signInResult = AuthState.AuthError(ErrorCode.INVALID_CREDENTIALS)
                )
            )
            advanceUntilIdle()

            viewModel.signIn("user@example.com", "wrong-password")
            advanceUntilIdle()

            assertEquals("Invalid email or password", viewModel.emailError.value)
            assertEquals("Invalid email or password", viewModel.passwordError.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `unexpected auth failure shows generic visible email error`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        try {
            val viewModel = AuthViewModel(
                RecordingAuthRepository(
                    signInResult = AuthState.AuthError(ErrorCode.UNEXPECTED_FAILURE)
                )
            )
            advanceUntilIdle()

            viewModel.signIn("user@example.com", "password")
            advanceUntilIdle()

            assertEquals("Something went wrong. Try again.", viewModel.emailError.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `supabase email validation failure maps to email field`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        try {
            val viewModel = AuthViewModel(
                RecordingAuthRepository(
                    signInResult = AuthState.AuthError(ErrorCode.VALIDATION_FAILED)
                )
            )
            advanceUntilIdle()

            viewModel.signIn("user@example.com", "password")
            advanceUntilIdle()

            assertEquals("Invalid email", viewModel.emailError.value)
            assertEquals("", viewModel.passwordError.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `weak password auth failure maps to password field`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        try {
            val viewModel = AuthViewModel(
                RecordingAuthRepository(
                    signUpResult = AuthState.AuthError(ErrorCode.WEAK_PASSWORD)
                )
            )
            advanceUntilIdle()

            viewModel.signUp("User", "user@example.com", "weak")
            advanceUntilIdle()

            assertEquals("", viewModel.emailError.value)
            assertEquals("Weak password", viewModel.passwordError.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `email send rate limit auth failure maps to visible email error`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        try {
            val viewModel = AuthViewModel(
                RecordingAuthRepository(
                    signUpResult = AuthState.AuthError(ErrorCode.OVER_EMAIL_SEND_RATE_LIMIT)
                )
            )
            advanceUntilIdle()

            viewModel.signUp("User", "user@example.com", "password")
            advanceUntilIdle()

            assertEquals("Too many attempts. Try again later.", viewModel.emailError.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `existing email sign-up auth failure maps to visible email error`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        try {
            val viewModel = AuthViewModel(
                RecordingAuthRepository(
                    signUpResult = AuthState.AuthError(ErrorCode.USER_ALREADY_EXISTS)
                )
            )
            advanceUntilIdle()

            viewModel.signUp("User", "user@example.com", "password")
            advanceUntilIdle()

            assertEquals("Email already in use", viewModel.emailError.value)
            assertEquals(AuthState.AuthError(ErrorCode.USER_ALREADY_EXISTS), viewModel.authState.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `sign-up exposes pending email confirmation state with normalized email`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        try {
            val viewModel = AuthViewModel(
                RecordingAuthRepository(
                    signUpResult = AuthState.EmailConfirmationRequired("user@example.com")
                )
            )
            advanceUntilIdle()

            viewModel.signUp("User", " user@example.com ", "password")
            advanceUntilIdle()

            assertEquals(AuthState.EmailConfirmationRequired("user@example.com"), viewModel.authState.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `successful email otp verification authenticates user`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        try {
            val repository = RecordingAuthRepository(
                verifyEmailOtpResult = AuthState.Authenticated
            )
            val viewModel = AuthViewModel(repository)
            advanceUntilIdle()

            viewModel.verifyEmailOtp("user@example.com", " 123456 ")
            advanceUntilIdle()

            assertEquals(
                EmailOtpRequest(
                    email = "user@example.com",
                    token = "123456"
                ),
                repository.verifyEmailOtpRequest
            )
            assertEquals(AuthState.Authenticated, viewModel.authState.value)
            assertEquals("", viewModel.otpError.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `invalid email otp shows visible verification error`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        try {
            val viewModel = AuthViewModel(
                RecordingAuthRepository(
                    verifyEmailOtpResult = AuthState.AuthError(ErrorCode.VALIDATION_FAILED)
                )
            )
            advanceUntilIdle()

            viewModel.verifyEmailOtp("user@example.com", "000000")
            advanceUntilIdle()

            assertEquals("Invalid or expired verification code", viewModel.otpError.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `expired email otp shows visible verification error`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        try {
            val viewModel = AuthViewModel(
                RecordingAuthRepository(
                    verifyEmailOtpResult = AuthState.AuthError(ErrorCode.OTP_EXPIRED)
                )
            )
            advanceUntilIdle()

            viewModel.verifyEmailOtp("user@example.com", "000000")
            advanceUntilIdle()

            assertEquals("Invalid or expired verification code", viewModel.otpError.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `blank email otp does not call repository`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        try {
            val repository = RecordingAuthRepository()
            val viewModel = AuthViewModel(repository)
            advanceUntilIdle()

            viewModel.verifyEmailOtp("user@example.com", "   ")
            advanceUntilIdle()

            assertEquals(0, repository.verifyEmailOtpCalls)
            assertEquals("Enter the verification code", viewModel.otpError.value)
            assertEquals(AuthState.Unauthenticated, viewModel.authState.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `resend sign-up email otp keeps pending state and exposes success message`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        try {
            val repository = RecordingAuthRepository(
                resendSignUpEmailOtpResult = AuthState.EmailConfirmationRequired("user@example.com")
            )
            val viewModel = AuthViewModel(repository)
            advanceUntilIdle()

            viewModel.resendSignUpEmailOtp("user@example.com")
            advanceUntilIdle()

            assertEquals("user@example.com", repository.resendSignUpEmailOtpRequest)
            assertEquals(AuthState.EmailConfirmationRequired("user@example.com"), viewModel.authState.value)
            assertEquals("Verification code resent", viewModel.otpInfo.value)
            assertEquals("", viewModel.otpError.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `dismiss email confirmation required clears pending auth state and otp messages`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        try {
            val viewModel = AuthViewModel(
                RecordingAuthRepository(
                    resendSignUpEmailOtpResult = AuthState.EmailConfirmationRequired("user@example.com")
                )
            )
            advanceUntilIdle()

            viewModel.resendSignUpEmailOtp("user@example.com")
            advanceUntilIdle()
            viewModel.dismissEmailConfirmationRequired()

            assertEquals(AuthState.Unauthenticated, viewModel.authState.value)
            assertEquals("", viewModel.otpError.value)
            assertEquals("", viewModel.otpInfo.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `resend sign-up email otp maps rate limit to visible verification error`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        try {
            val viewModel = AuthViewModel(
                RecordingAuthRepository(
                    resendSignUpEmailOtpResult = AuthState.AuthError(ErrorCode.OVER_REQUEST_RATE_LIMIT)
                )
            )
            advanceUntilIdle()

            viewModel.resendSignUpEmailOtp("user@example.com")
            advanceUntilIdle()

            assertEquals("Too many attempts. Try again later.", viewModel.otpError.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `resend sign-up email otp maps email send rate limit to visible verification error`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        try {
            val viewModel = AuthViewModel(
                RecordingAuthRepository(
                    resendSignUpEmailOtpResult = AuthState.AuthError(ErrorCode.OVER_EMAIL_SEND_RATE_LIMIT)
                )
            )
            advanceUntilIdle()

            viewModel.resendSignUpEmailOtp("user@example.com")
            advanceUntilIdle()

            assertEquals("Too many attempts. Try again later.", viewModel.otpError.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    private class FailingBootstrapAuthRepository : AuthRepository {
        override suspend fun signUpWithEmail(displayName: String, email: String, password: String): AuthState {
            return AuthState.Unauthenticated
        }

        override suspend fun signInWithEmail(email: String, password: String): AuthState {
            return AuthState.Unauthenticated
        }

        override suspend fun signUpWithGoogle(idToken: String, nonce: String?): AuthState {
            return AuthState.Unauthenticated
        }

        override suspend fun signInWithGoogle(idToken: String, nonce: String?): AuthState {
            return AuthState.Unauthenticated
        }

        override suspend fun verifyEmailOtp(email: String, token: String): AuthState {
            return AuthState.Unauthenticated
        }

        override suspend fun resendSignUpEmailOtp(email: String): AuthState {
            return AuthState.Unauthenticated
        }

        override fun observeAuthState() = flow<AuthState> {
            error("Session observation failed")
        }

        override fun getCurrentUser(): User? {
            error("Session lookup failed")
        }

        override fun signOut() {}
    }

    private class RecordingAuthRepository(
        private val signInResult: AuthState = AuthState.Authenticated,
        private val signUpResult: AuthState = AuthState.Authenticated,
        private val verifyEmailOtpResult: AuthState = AuthState.Authenticated,
        private val resendSignUpEmailOtpResult: AuthState = AuthState.Unauthenticated,
        private val observedAuthStates: List<AuthState> = listOf(AuthState.Unauthenticated),
    ) : AuthRepository {
        var signInCalls = 0
            private set
        var signUpCalls = 0
            private set
        var verifyEmailOtpCalls = 0
            private set
        var verifyEmailOtpRequest: EmailOtpRequest? = null
            private set
        var resendSignUpEmailOtpRequest: String? = null
            private set

        override suspend fun signUpWithEmail(displayName: String, email: String, password: String): AuthState {
            signUpCalls += 1
            return signUpResult
        }

        override suspend fun signInWithEmail(email: String, password: String): AuthState {
            signInCalls += 1
            return signInResult
        }

        override suspend fun signUpWithGoogle(idToken: String, nonce: String?): AuthState {
            return AuthState.Unauthenticated
        }

        override suspend fun signInWithGoogle(idToken: String, nonce: String?): AuthState {
            return AuthState.Unauthenticated
        }

        override suspend fun verifyEmailOtp(email: String, token: String): AuthState {
            verifyEmailOtpCalls += 1
            verifyEmailOtpRequest = EmailOtpRequest(email, token)
            return verifyEmailOtpResult
        }

        override suspend fun resendSignUpEmailOtp(email: String): AuthState {
            resendSignUpEmailOtpRequest = email
            return resendSignUpEmailOtpResult
        }

        override fun observeAuthState() = flowOf(*observedAuthStates.toTypedArray())

        override fun getCurrentUser(): User? = null

        override fun signOut() {}
    }

    private data class EmailOtpRequest(
        val email: String,
        val token: String,
    )
}
