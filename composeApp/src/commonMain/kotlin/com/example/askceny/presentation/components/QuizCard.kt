package com.example.askceny.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import com.example.askceny.presentation.preview.SampleUiState
import com.example.askceny.presentation.state.QuizUiModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun QuizCard(
    quiz: QuizUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            AsyncImage(
                model = quiz.imageUrl,
                contentDescription = "Cover",
                contentScale = ContentScale.Crop,
                transform = AsyncImagePainter.DefaultTransform,
                modifier = Modifier
                    .height(100.dp),
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                fontSize = 26.sp,
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.ExtraBold,
                text = quiz.title,
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                fontSize = 22.sp,
                text = quiz.description,
                fontWeight = FontWeight.Light,
                maxLines = 3,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizCardPreview() {
    QuizCard(
        quiz = SampleUiState.sampleQuizzes.first(),
        onClick = {},
    )
}
