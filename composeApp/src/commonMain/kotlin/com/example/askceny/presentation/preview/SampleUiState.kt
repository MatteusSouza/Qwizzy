package com.example.askceny.presentation.preview

import com.example.askceny.presentation.state.EditQuizUiState
import com.example.askceny.presentation.state.QuizDetailUiState
import com.example.askceny.presentation.state.QuizUiModel
import com.example.askceny.presentation.state.QuizzesListUiState
import com.example.askceny.presentation.state.SignInUiState
import com.example.askceny.presentation.state.SignUpUiState
import com.example.askceny.presentation.state.VerifyEmailUiState

object SampleUiState {
    val signIn = SignInUiState(
        email = "user@example.com",
        password = "password123",
    )

    val signInWithErrors = SignInUiState(
        email = "user@example.com",
        password = "wrong",
        emailError = " ",
        passwordError = " ",
    )

    val signUp = SignUpUiState(
        displayName = "Ceny User",
        email = "user@example.com",
        password = "password123",
    )

    val verifyEmail = VerifyEmailUiState(
        email = "user@example.com",
        code = "123456",
    )

    val sampleQuizzes = listOf(
        QuizUiModel(
            id = "history",
            title = "History Basics",
            description = "A short quiz about important history milestones.",
            imageUrl = "https://picsum.photos/seed/history/800/400",
        ),
        QuizUiModel(
            id = "science",
            title = "Science Warmup",
            description = "Questions about biology, physics, and chemistry.",
            imageUrl = "https://picsum.photos/seed/science/800/400",
        ),
    )

    val quizzesList = QuizzesListUiState(quizzes = sampleQuizzes)

    val quizDetail = QuizDetailUiState(quiz = sampleQuizzes.first())

    val editQuiz = EditQuizUiState(
        imageUrl = sampleQuizzes.first().imageUrl,
        title = sampleQuizzes.first().title,
        description = sampleQuizzes.first().description,
        isEditing = true,
    )

    val createQuiz = EditQuizUiState()
}
