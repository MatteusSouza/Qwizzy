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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import com.example.askceny.presentation.preview.SampleUiState
import com.example.askceny.presentation.state.EditQuizUiState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EditQuizContent(
    state: EditQuizUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    Column(modifier = modifier.verticalScroll(scrollState).fillMaxSize()) {
        AsyncImage(
            model = state.imageUrl,
            contentDescription = "Cover",
            contentScale = ContentScale.Crop,
            transform = AsyncImagePainter.DefaultTransform,
            modifier = Modifier.fillMaxWidth()
                .height(200.dp),
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 80.dp),
                value = state.title,
                onValueChange = onTitleChange,
                label = { Text("Title") },
            )
            TextField(
                modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 152.dp),
                value = state.description,
                onValueChange = onDescriptionChange,
                label = { Text("Description") },
            )
            Button(onClick = onSaveClick) {
                Text("Save")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditQuizContentPreview() {
    EditQuizContent(
        state = SampleUiState.editQuiz,
        onTitleChange = {},
        onDescriptionChange = {},
        onSaveClick = {},
    )
}
