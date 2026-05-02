package com.example.askceny

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.askceny.domain.types.AuthState
import com.example.askceny.presentation.composables.CustomTopBar
import com.example.askceny.presentation.composables.EditQuiz
import com.example.askceny.presentation.composables.QuizDetail
import com.example.askceny.presentation.composables.QuizzesList
import com.example.askceny.presentation.composables.SignInScreen
import com.example.askceny.presentation.composables.SignUpScreen
import com.example.askceny.presentation.theme.AskCenyTheme
import com.example.askceny.presentation.viewmodels.AuthViewModel
import com.example.askceny.presentation.viewmodels.QuizViewModel

@Composable
fun App(
    authViewModel: AuthViewModel,
    quizViewModel: QuizViewModel,
) {
    AskCenyTheme {
        val focusManager = LocalFocusManager.current

        MainScreen(
            modifier = Modifier.clearFocusOnTap(focusManager),
            authViewModel = authViewModel,
            quizViewmodel = quizViewModel,
        )
    }
}

private fun Modifier.clearFocusOnTap(focusManager: FocusManager): Modifier =
    pointerInput(focusManager) {
        awaitEachGesture {
            awaitFirstDown(pass = PointerEventPass.Initial)
            if (waitForUpOrCancellation(pass = PointerEventPass.Initial) != null) {
                focusManager.clearFocus()
            }
        }
    }

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    quizViewmodel: QuizViewModel,
) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: ""

    val authState by authViewModel.authState.collectAsState()
    println("MAIN_SCREEN_VERIFY_AUTHENTICATION:  ${ authState is AuthState.Authenticated }")

    var isAppOnStart: Boolean by rememberSaveable { mutableStateOf(true) }
    val startDestination: String = if (authState is AuthState.Authenticated) "QuizzesList" else "SignIn"

    if (isAppOnStart) {
        isAppOnStart = false
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            MainTopBar(
                currentRoute = currentRoute,
                navController = navController,
                authViewModel = authViewModel,
            )
        },
    ) { innerPadding ->
        MyNavHost(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
            navController = navController,
            startDestination = startDestination,
            authViewModel = authViewModel,
            quizViewmodel = quizViewmodel,
        )
    }
}

@Composable
private fun MainTopBar(
    currentRoute: String,
    navController: NavHostController,
    authViewModel: AuthViewModel,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Top + WindowInsetsSides.Horizontal
                )
            ),
        horizontalArrangement = Arrangement.End,
    ) {
        when (currentRoute) {
            "SignIp" -> {
                println("NAVIGATION_ROUTE: Sign In ")
            }

            "SignUp" -> {
                println("NAVIGATION_ROUTE: Sign Up ")
            }

            "QuizzesList" -> {
                CustomTopBar(
                    modifier = Modifier,
                    title = "Quizzes",
                    showSearchButton = true,
                    onClickSearch = { navController.navigate("Search") },
                    onClickLogout = { authViewModel.signOut() },
                )
                println("NAVIGATION_ROUTE: QuizList")
            }

            "QuizDetail" -> {
                CustomTopBar(
                    modifier = Modifier,
                    showBackButton = true,
                    showEditButton = true,
                    onClickBackPressed = { navController.popBackStack() },
                    onClickEdit = { navController.navigate("EditQuiz") },
                )
                println("NAVIGATION_ROUTE: QuizDetail")
            }

            "EditQuiz" -> {
                CustomTopBar(
                    modifier = Modifier,
                    showBackButton = true,
                    onClickBackPressed = { navController.popBackStack() },
                    onClickSearch = { navController.navigate("Search") },
                )
                println("NAVIGATION_ROUTE: QuizEdit")
            }

            "Search" -> {
                CustomTopBar(
                    modifier = Modifier,
                    showBackButton = true,
                    onClickBackPressed = { navController.popBackStack() },
                )
                println("NAVIGATION_ROUTE: QuizEdit")
            }

            else -> Unit
        }
    }
}

@Composable
fun MyNavHost(
    modifier: Modifier,
    navController: NavHostController,
    startDestination: String,
    authViewModel: AuthViewModel,
    quizViewmodel: QuizViewModel,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable("SignIn") {
            SignInScreen(
                modifier = Modifier,
                viewModel = authViewModel,
                signInOnClick = {
                    navController.navigate("QuizzesList") {
                        popUpTo("SignIn") { inclusive = true }
                    }
                },
                signUpOnClick = { navController.navigate("SignUp") },
            )
        }
        composable("SignUp") {
            SignUpScreen(
                modifier = Modifier,
                viewModel = authViewModel,
                signUpOnClick = {
                    navController.navigate("QuizzesList") {
                        popUpTo("SignIn") { inclusive = true }
                    }
                },
                signInOnClick = {
                    navController.popBackStack()
                },
            )
        }
        composable("QuizzesList") {
            QuizzesList(
                viewModel = quizViewmodel,
                modifier = Modifier,
                onClickItem = { navController.navigate("QuizDetail") },
                onClickAddQuiz = { navController.navigate("EditQuiz") },
            )
        }
        composable("QuizDetail") {
            QuizDetail(modifier = Modifier, viewModel = quizViewmodel)
        }
        composable("EditQuiz") {
            EditQuiz(
                modifier = Modifier,
                viewModel = quizViewmodel,
                onBackButton = { navController.popBackStack() },
            )
        }
        composable("EditQuestion") { Text("EditQuestion") }
        composable("PlayQuiz") { Text("PlayQuiz") }
        composable("Result") { Text("Result") }
        composable("Search") {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Search Test", fontSize = 32.sp)
            }
        }
    }
}
