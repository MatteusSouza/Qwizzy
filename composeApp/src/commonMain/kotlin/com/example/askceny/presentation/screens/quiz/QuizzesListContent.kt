package com.example.askceny.presentation.screens.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.askceny.presentation.components.QuizCard
import com.example.askceny.presentation.preview.SampleUiState
import com.example.askceny.presentation.state.QuizUiModel
import com.example.askceny.presentation.state.QuizzesListUiState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun QuizzesListContent(
    state: QuizzesListUiState,
    onQuizClick: (QuizUiModel) -> Unit,
    onAddQuizClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(state.quizzes) { quiz ->
                QuizCard(
                    quiz = quiz,
                    onClick = { onQuizClick(quiz) },
                )
            }
        }

        FloatingActionButton(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd),
            onClick = onAddQuizClick,
        ) {
            Icon(Icons.Filled.Add, "Create quiz button")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizzesListContentPreview() {
    QuizzesListContent(
        state = SampleUiState.quizzesList,
        onQuizClick = {},
        onAddQuizClick = {},
    )
}
