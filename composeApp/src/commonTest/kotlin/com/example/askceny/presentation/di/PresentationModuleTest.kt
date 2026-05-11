package com.example.askceny.presentation.di

import com.example.askceny.domain.models.Question
import com.example.askceny.domain.models.Quiz
import com.example.askceny.domain.models.User
import com.example.askceny.domain.repositories.AuthRepository
import com.example.askceny.domain.repositories.QuizRepository
import com.example.askceny.domain.types.AuthState
import com.example.askceny.domain.types.QuestionType
import com.example.askceny.presentation.viewmodels.AuthViewModel
import com.example.askceny.presentation.viewmodels.QuizViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

@OptIn(ExperimentalCoroutinesApi::class)
class PresentationModuleTest {
    @Test
    fun `presentation module resolves view models`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        try {
            val koin = startKoin {
                modules(
                    module {
                        single<AuthRepository> { TestAuthRepository() }
                        single<QuizRepository> { TestQuizRepository() }
                    },
                    presentationModule,
                )
            }.koin

            val authViewModel: AuthViewModel = koin.get()
            val quizViewModel: QuizViewModel = koin.get()
            advanceUntilIdle()

            assertEquals(AuthState.Unauthenticated, authViewModel.authState.value)
            assertIs<QuizViewModel>(quizViewModel)
        } finally {
            stopKoin()
            Dispatchers.resetMain()
        }
    }

    private class TestAuthRepository : AuthRepository {
        override suspend fun signUpWithEmail(displayName: String, email: String, password: String): AuthState {
            return AuthState.Authenticated
        }

        override suspend fun signInWithEmail(email: String, password: String): AuthState {
            return AuthState.Authenticated
        }

        override suspend fun signUpWithGoogle(idToken: String, nonce: String?): AuthState {
            return AuthState.Authenticated
        }

        override suspend fun signInWithGoogle(idToken: String, nonce: String?): AuthState {
            return AuthState.Authenticated
        }

        override suspend fun verifyEmailOtp(email: String, token: String): AuthState {
            return AuthState.Authenticated
        }

        override suspend fun resendSignUpEmailOtp(email: String): AuthState {
            return AuthState.EmailConfirmationRequired(email)
        }

        override fun getCurrentUser(): User? = null

        override fun signOut() {}
    }

    private class TestQuizRepository : QuizRepository {
        override suspend fun createQuiz(title: String, description: String, img: String, isPublic: Boolean) {}

        override suspend fun getAllQuizzes(): List<Quiz> = emptyList()

        override suspend fun editQuiz(quizId: String, quizUpdateMap: Map<String, Any>) {}

        override fun deleteQuiz(quizId: String) {}

        override fun createQuestion(quizId: String, text: String, type: QuestionType) {}

        override fun getQuizAllQuestions(quizId: String): MutableList<Question>? = null

        override fun deleteQuestion(questionId: String) {}
    }
}
