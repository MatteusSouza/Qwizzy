package com.example.askceny.presentation.screens.quiz

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.askceny.presentation.state.QuizzesListUiState
import com.example.askceny.presentation.state.toUiModel
import com.example.askceny.presentation.viewmodels.QuizViewModel

@Composable
fun QuizzesListScreen(
    viewModel: QuizViewModel,
    modifier: Modifier = Modifier,
    onClickItem: () -> Unit,
    onClickAddQuiz: () -> Unit,
) {
    // So many request. Remember to never do that. Uses LaunchedEffect
    LaunchedEffect(Unit) {
        viewModel.update()
    }

    val quizzesState by viewModel.quizzesState.collectAsState()

    QuizzesListContent(
        modifier = modifier,
        state = QuizzesListUiState(
            quizzes = quizzesState.map { it.toUiModel() },
        ),
        onQuizClick = { selectedQuiz ->
            val quiz = quizzesState.firstOrNull { it.id == selectedQuiz.id }
            println("QUIZ_DETAIL: QUIZ_LIST Instance of viewmodel: $viewModel")
            viewModel.setQuizInFocus(quiz)
            println("QUIZZES_LIST.QUIZ_ROW: onFocus ${viewModel.getQuizInFocus()}")
            onClickItem()
        },
        onAddQuizClick = {
            viewModel.setQuizInFocus(null)
            println("QUIZZES_LIST.QUIZ_ROW: onFocus ${viewModel.getQuizInFocus()}")
            onClickAddQuiz()
        },
    )
}
