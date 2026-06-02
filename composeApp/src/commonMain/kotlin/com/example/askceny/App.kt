package com.example.askceny

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.askceny.presentation.components.CustomTopBar
import com.example.askceny.presentation.screens.auth.SignInScreen
import com.example.askceny.presentation.screens.auth.SignUpScreen
import com.example.askceny.presentation.screens.auth.VerifyEmailScreen
import com.example.askceny.presentation.screens.quiz.EditQuizScreen
import com.example.askceny.presentation.screens.quiz.QuizDetailScreen
import com.example.askceny.presentation.screens.quiz.QuizzesListScreen
import com.example.askceny.presentation.di.koinViewModel
import com.example.askceny.presentation.theme.AskCenyTheme
import com.example.askceny.presentation.viewmodels.AuthViewModel
import com.example.askceny.presentation.viewmodels.QuizViewModel

@Composable
fun App() {
    val authViewModel: AuthViewModel = koinViewModel()
    val quizViewModel: QuizViewModel = koinViewModel()

    AskCenyTheme {
        val focusManager = LocalFocusManager.current

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .clearFocusOnTap(focusManager),
        ) { innerPadding ->
            MainScreen(
                innerPadding = innerPadding,
                authViewModel = authViewModel,
                quizViewmodel = quizViewModel,
            )
        }
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
    innerPadding: PaddingValues,
    authViewModel: AuthViewModel,
    quizViewmodel: QuizViewModel,
) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: ""

    val authState by authViewModel.authState.collectAsState()
    println("MAIN_SCREEN_VERIFY_AUTHENTICATION:  ${ authState is AuthState.Authenticated }")

    var isAppOnStart: Boolean by rememberSaveable { mutableStateOf(true) }
    var pendingVerificationEmail: String by rememberSaveable { mutableStateOf("") }
    val startDestination: String = when (authState) {
        AuthState.Authenticated -> "QuizzesList"
        AuthState.Loading -> "AuthLoading"
        else -> "SignIn"
    }

    if (isAppOnStart) {
        isAppOnStart = false
    }

    LaunchedEffect(authState, currentRoute) {
        when (authState) {
            AuthState.Authenticated -> {
                if (currentRoute == "AuthLoading") {
                    navController.navigateAndClearBackStack(
                        route = "QuizzesList",
                        popUpToRoute = "AuthLoading",
                    )
                }
            }
            AuthState.Unauthenticated -> {
                when (currentRoute) {
                    "AuthLoading" -> {
                        navController.navigateAndClearBackStack(
                            route = "SignIn",
                            popUpToRoute = "AuthLoading",
                        )
                    }
                    "QuizzesList",
                    "QuizDetail",
                    "EditQuiz",
                    "EditQuestion",
                    "PlayQuiz",
                    "Result",
                    "Search" -> {
                        navController.navigateAndClearBackStack(
                            route = "SignIn",
                            popUpToRoute = "QuizzesList",
                        )
                    }
                }
            }
            is AuthState.AuthError -> {
                if (currentRoute == "AuthLoading") {
                    navController.navigateAndClearBackStack(
                        route = "SignIn",
                        popUpToRoute = "AuthLoading",
                    )
                }
            }
            else -> Unit
        }
    }

    Column(modifier = Modifier.padding(innerPadding)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            when (currentRoute) {
                "SignIp" -> {
                    println("NAVIGATION_ROUTE: Sign In ")
                }

                "SignUp" -> {
                    println("NAVIGATION_ROUTE: Sign Up ")
                }

                "VerifyEmail" -> {
                    println("NAVIGATION_ROUTE: Verify Email ")
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
        MyNavHost(
            innerPadding = innerPadding,
            navController = navController,
            startDestination = startDestination,
            authViewModel = authViewModel,
            quizViewmodel = quizViewmodel,
            pendingVerificationEmail = pendingVerificationEmail,
            onPendingVerificationEmailChange = { pendingVerificationEmail = it },
        )
    }
}

@Composable
fun MyNavHost(
    innerPadding: PaddingValues,
    navController: NavHostController,
    startDestination: String,
    authViewModel: AuthViewModel,
    quizViewmodel: QuizViewModel,
    pendingVerificationEmail: String,
    onPendingVerificationEmailChange: (String) -> Unit,
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable("AuthLoading") {}
        composable("SignIn") {
            SignInScreen(
                modifier = Modifier.padding(innerPadding),
                viewModel = authViewModel,
                signInOnClick = {
                    navController.navigate("QuizzesList") {
                        popUpTo("SignIn") { inclusive = true }
                    }
                },
                signUpOnClick = {
                    authViewModel.dismissEmailConfirmationRequired()
                    onPendingVerificationEmailChange("")
                    navController.navigate("SignUp")
                },
            )
        }
        composable("SignUp") {
            SignUpScreen(
                modifier = Modifier.padding(innerPadding),
                viewModel = authViewModel,
                signUpOnClick = {
                    navController.navigate("QuizzesList") {
                        popUpTo("SignIn") { inclusive = true }
                    }
                },
                signInOnClick = {
                    authViewModel.dismissEmailConfirmationRequired()
                    onPendingVerificationEmailChange("")
                    navController.popBackStack()
                },
                onEmailConfirmationRequired = { email ->
                    onPendingVerificationEmailChange(email)
                    navController.navigate("VerifyEmail") {
                        launchSingleTop = true
                    }
                },
            )
        }
        composable("VerifyEmail") {
            VerifyEmailScreen(
                modifier = Modifier.padding(innerPadding),
                email = pendingVerificationEmail,
                viewModel = authViewModel,
                onVerified = {
                    navController.navigate("QuizzesList") {
                        popUpTo("SignIn") { inclusive = true }
                    }
                },
                onBackToSignIn = {
                    authViewModel.dismissEmailConfirmationRequired()
                    onPendingVerificationEmailChange("")
                    navController.navigate("SignIn") {
                        popUpTo("SignIn") { inclusive = true }
                    }
                },
            )
        }
        composable("QuizzesList") {
            QuizzesListScreen(
                viewModel = quizViewmodel,
                modifier = Modifier,
                onClickItem = { navController.navigate("QuizDetail") },
                onClickAddQuiz = { navController.navigate("EditQuiz") },
            )
        }
        composable("QuizDetail") {
            QuizDetailScreen(modifier = Modifier, viewModel = quizViewmodel)
        }
        composable("EditQuiz") {
            EditQuizScreen(
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

private fun NavHostController.navigateAndClearBackStack(
    route: String,
    popUpToRoute: String,
) {
    navigate(route) {
        popUpTo(popUpToRoute) { inclusive = true }
        launchSingleTop = true
    }
}
