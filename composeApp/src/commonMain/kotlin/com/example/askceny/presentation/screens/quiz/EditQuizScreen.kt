package com.example.askceny.presentation.screens.quiz

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.askceny.presentation.state.EditQuizUiState
import com.example.askceny.presentation.viewmodels.QuizViewModel

@Composable
fun EditQuizScreen(
    modifier: Modifier,
    viewModel: QuizViewModel,
    onBackButton: () -> Unit,
) {
    val quizInFocus by viewModel.quizInFocus.collectAsState()
    val formKey = quizInFocus?.id ?: "create"
    var title: String by rememberSaveable(formKey) { mutableStateOf(quizInFocus?.title ?: "") }
    var description: String by rememberSaveable(formKey) { mutableStateOf(quizInFocus?.description ?: "") }

    EditQuizContent(
        modifier = modifier,
        state = EditQuizUiState(
            imageUrl = quizInFocus?.img,
            title = title,
            description = description,
            isEditing = quizInFocus != null,
        ),
        onTitleChange = { title = it },
        onDescriptionChange = { description = it },
        onSaveClick = {
            println("EDIT_QUIZ.OnClick: onFocus ${viewModel.getQuizInFocus()}")
            println("EDIT_QUIZ.OnClick: title: $title description: $description")
            viewModel.saveQuiz(title = title, description = description)
            onBackButton()
        },
    )
}
