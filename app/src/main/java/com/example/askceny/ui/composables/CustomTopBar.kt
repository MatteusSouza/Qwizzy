package com.example.askceny.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun CustomTopBar(
    modifier: Modifier,
    title: String = "",
    onClickBackPressed: () -> Unit = { },
    onClickSearch: () -> Unit = { },
    onClickEdit: () -> Unit = { },
    onClickMenu: () -> Unit = { },
    onClickLogout: () -> Unit = { },
    showBackButton: Boolean = false,
    showSearchButton: Boolean = false,
    showEditButton: Boolean = false,
    showMenuButton: Boolean = false,
    onSearchMode: Boolean = false
    ) {
    val onSearchMode by rememberSaveable { mutableStateOf(onSearchMode) }

    var isLandscape = isLandscape()
    var innerPadding = PaddingValues(6.dp)
    if (isLandscape) {
        innerPadding = PaddingValues(top = 2.dp, bottom = 2.dp, start = 6.dp, end = 6.dp)
    }

    if (onSearchMode == true) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(modifier = Modifier.padding(innerPadding),onClick = onClickBackPressed) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription  = "Go Back"
                )
            }
        }
    } else {
        Row(modifier = Modifier.fillMaxWidth()
            , verticalAlignment = Alignment.CenterVertically) {
            if (showBackButton) {
                IconButton(modifier = Modifier.padding(innerPadding),onClick = onClickBackPressed) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription  = "Go Back"
                    )
                }
            }
            Text(
                modifier = modifier
                    .padding(innerPadding).padding(PaddingValues(start = 18.dp))
                    .weight(1f),
                color = Color.Black, text = title,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (showSearchButton) {
                IconButton(modifier = Modifier.padding(innerPadding),onClick = onClickSearch) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription  = "Search"
                    )
                }
            }
            if (showEditButton) {
                IconButton(modifier = Modifier.padding(innerPadding),onClick = onClickEdit) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription  = "Edit"
                    )
                }
            }
            if (showMenuButton) {
                IconButton(modifier = Modifier.padding(innerPadding), onClick = onClickMenu) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription  = "Options"
                    )
                }
            }
            TextButton(onClick = onClickLogout) { Text("Logout") } // Just for testing until the user screen is implemented and then it will be removed
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomTopBarPreview() {
    Column {
        CustomTopBar(modifier = Modifier, title = "Title",  showBackButton = true)
        CustomTopBar(modifier = Modifier, title = "Title", showSearchButton = true, showMenuButton = true)
        CustomTopBar(modifier = Modifier, showBackButton = true, showEditButton = true)
    }
}