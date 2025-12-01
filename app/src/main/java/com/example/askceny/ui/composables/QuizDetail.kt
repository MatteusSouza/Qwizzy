package com.example.askceny.ui.composables

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import com.example.askceny.data.models.Quiz
import com.example.askceny.ui.viewmodels.QuizViewModel

@Composable
fun QuizDetail(
    modifier: Modifier,
    viewModel: QuizViewModel
) {

    val quizInFocus by viewModel.quizInFocus.collectAsState()

    val quiz : Quiz = quizInFocus ?: Quiz("", "", "")
    val scrollState = rememberScrollState()

    Column(modifier = modifier.padding(16.dp).verticalScroll(scrollState)) {
        AsyncImage(
            model = quiz.img, /*"https://avatars.githubusercontent.com/u/61070878",*/
            contentDescription = "Cover",
            contentScale = ContentScale.Crop,
            transform = AsyncImagePainter.DefaultTransform,
            modifier = Modifier.fillMaxWidth()
                .height(200.dp),
        )
        Column {
//            val quiz : Quiz = viewModel.getQuizInFocus() ?: Quiz("", "", "")

            Text(text = quiz.title, fontSize = 36.sp,modifier = Modifier.padding(paddingValues = PaddingValues(bottom = 6.dp)))
//            Spacer(modifier = Modifier.height(32.dp))

            Text("Description",fontSize = 24.sp,modifier = Modifier.padding(paddingValues = PaddingValues(bottom = 6.dp)))
            Text(quiz.description)
            Spacer(modifier = Modifier.width(8.dp))

            println("QUIZ_DETAIL: title .. ${quiz.title}")
            println("QUIZ_DETAIL: Instance of viewmodel: $viewModel")
        }

    }
}