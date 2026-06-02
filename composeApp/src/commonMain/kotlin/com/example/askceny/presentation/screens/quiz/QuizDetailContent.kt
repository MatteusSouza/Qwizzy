package com.example.askceny.presentation.screens.quiz

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import com.example.askceny.presentation.preview.SampleUiState
import com.example.askceny.presentation.state.QuizDetailUiState
import com.example.askceny.presentation.state.QuizUiModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun QuizDetailContent(
    state: QuizDetailUiState,
    modifier: Modifier = Modifier,
) {
    val quiz = state.quiz ?: QuizUiModel("", "", "", "")
    val scrollState = rememberScrollState()

    Column(modifier = modifier.padding(16.dp).verticalScroll(scrollState)) {
        AsyncImage(
            model = quiz.imageUrl,
            contentDescription = "Cover",
            contentScale = ContentScale.Crop,
            transform = AsyncImagePainter.DefaultTransform,
            modifier = Modifier.fillMaxWidth()
                .height(200.dp),
        )
        Column {
            Text(
                text = quiz.title,
                fontSize = 36.sp,
                modifier = Modifier.padding(paddingValues = PaddingValues(bottom = 6.dp)),
            )
            Text(
                "Description",
                fontSize = 24.sp,
                modifier = Modifier.padding(paddingValues = PaddingValues(bottom = 6.dp)),
            )
            Text(quiz.description)
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizDetailContentPreview() {
    QuizDetailContent(state = SampleUiState.quizDetail)
}
