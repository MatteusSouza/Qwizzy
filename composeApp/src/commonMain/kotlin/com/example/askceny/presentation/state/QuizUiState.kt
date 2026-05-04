package com.example.askceny.presentation.state

import com.example.askceny.domain.models.Quiz

data class QuizUiModel(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
)

data class QuizzesListUiState(
    val quizzes: List<QuizUiModel> = emptyList(),
)

data class QuizDetailUiState(
    val quiz: QuizUiModel? = null,
)

data class EditQuizUiState(
    val imageUrl: String? = null,
    val title: String = "",
    val description: String = "",
    val isEditing: Boolean = false,
)

fun Quiz.toUiModel(): QuizUiModel =
    QuizUiModel(
        id = id,
        title = title,
        description = description,
        imageUrl = img,
    )
