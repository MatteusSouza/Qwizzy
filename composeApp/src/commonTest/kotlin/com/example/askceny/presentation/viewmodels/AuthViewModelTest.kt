package com.example.askceny.presentation.viewmodels

import com.example.askceny.domain.models.User
import com.example.askceny.domain.repositories.AuthRepository
import com.example.askceny.domain.types.AuthState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

        override fun getCurrentUser(): User? {
            error("Session lookup failed")
        }

        override fun signOut() {}
    }
}
