package com.example.askceny.presentation.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.askceny.presentation.state.QuizDetailUiState
import com.example.askceny.presentation.state.toUiModel
import com.example.askceny.presentation.viewmodels.QuizViewModel

@Composable
fun QuizDetailScreen(
    modifier: Modifier,
    viewModel: QuizViewModel,
) {
    val quizInFocus by viewModel.quizInFocus.collectAsState()

    QuizDetailContent(
        modifier = modifier,
        state = QuizDetailUiState(
            quiz = quizInFocus?.toUiModel(),
        ),
    )

    println("QUIZ_DETAIL: title .. ${quizInFocus?.title ?: ""}")
    println("QUIZ_DETAIL: Instance of viewmodel: $viewModel")
}
