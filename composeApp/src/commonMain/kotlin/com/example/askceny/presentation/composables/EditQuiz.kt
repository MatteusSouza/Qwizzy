package com.example.askceny.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import com.example.askceny.presentation.viewmodels.QuizViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EditQuiz(modifier: Modifier, viewModel: QuizViewModel, onBackButton: () -> Unit) {
    val scrollState = rememberScrollState()
    val imgUrl = viewModel.getQuizInFocus()?.img
    var title : String by rememberSaveable { mutableStateOf(viewModel.getQuizInFocus()?.title ?: "") }
    var description : String by rememberSaveable { mutableStateOf(viewModel.getQuizInFocus()?.description ?: "") }

    Column(modifier = modifier.verticalScroll(scrollState).fillMaxSize()) {
        AsyncImage(
            model = imgUrl,
            contentDescription = "Cover",
            contentScale = ContentScale.Crop,
            transform = AsyncImagePainter.DefaultTransform,
            modifier = Modifier.fillMaxWidth()
                .height(200.dp),
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 80.dp),
                value = title,
                onValueChange = { newText ->
                    title = newText
                },
                label = { Text("Title") },
            )
            TextField(
                modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 152.dp),
                value = description,
                onValueChange = { newText ->
                    description = newText
                },
                label = { Text("Description") },
            )
            Button(onClick = {
                println("EDIT_QUIZ.OnClick: onFocus ${viewModel.getQuizInFocus()}")
                println("EDIT_QUIZ.OnClick: title: $title description: $description")
                viewModel.saveQuiz(title = title, description = description)
                onBackButton()
            }) {
                Text("Save")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun EditQuizPreview() {
//    EditQuiz(modifier = Modifier, viewModel = QuizViewModel(QuizRepositoryFake()), onBackButton = {})
}
