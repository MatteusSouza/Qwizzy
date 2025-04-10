package com.example.askceny.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import com.example.askceny.data.models.Quiz
import com.example.askceny.data.repositories.QuizRepositoryFake
import com.example.askceny.ui.viewmodels.QuizViewModel

@Composable
fun QuizzesList(
    viewModel: QuizViewModel,
    modifier: Modifier,
    onClickItem: () -> Unit,
    onClickAddQuiz: () -> Unit
) {
    viewModel.update()
    val quizzesState by viewModel.quizzesState.collectAsState()

    Box(modifier = modifier
        .fillMaxSize()
    ) {
        LazyColumn (
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items (quizzesState.size) { index ->
                val quiz = quizzesState[index]
                QuizRow(viewModel, quizItem = quiz, onClick = {onClickItem()})
            }
        }

        FloatingActionButton(
            modifier = modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd),
            onClick = {

                viewModel.setQuizInFocus(null)
                println("QUIZZES_LIST.QUIZ_ROW: onFocus ${viewModel.getQuizInFocus()}")
                onClickAddQuiz()
            },
        ) {
            Icon(Icons.Filled.Add, "Create quiz button")
        }

    }
}

@Composable
fun QuizRow(viewModel: QuizViewModel, quizItem: Quiz, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            println("QUIZ_DETAIL: QUIZ_LIST Instance of viewmodel: $viewModel")
            viewModel.setQuizInFocus(quizItem)
            println("QUIZZES_LIST.QUIZ_ROW: onFocus ${viewModel.getQuizInFocus()}")
            onClick()
        }) {
        Column(modifier = Modifier
            .fillMaxWidth()
//            .height(120.dp)
        )
        {
            AsyncImage(
                model = quizItem.img,
                contentDescription = "Cover",
                contentScale = ContentScale.Crop,
                transform = AsyncImagePainter.DefaultTransform,
                modifier = Modifier
                    .height(100.dp),
            )
            /*
            Icon(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxHeight()
                    .width(56.dp),
                imageVector = Icons.Filled.AccountCircle, contentDescription = "dataImage"
            )
             */
            Text(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
//                .align(Alignment.CenterVertically),
                fontSize = 26.sp,
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.ExtraBold,
                text = quizItem.title,
            )
            Text(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
//                .align(Alignment.CenterVertically),
                fontSize = 22.sp,
                text = quizItem.description,
                fontWeight = FontWeight.Light,
                maxLines = 3
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizzesListPreview() {
    QuizzesList(
        viewModel = QuizViewModel(QuizRepositoryFake()),
        modifier = Modifier,
        onClickItem = {},
        onClickAddQuiz = {}
    )
}