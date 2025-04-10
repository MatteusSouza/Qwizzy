package com.example.askceny

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.askceny.data.repositories.AuthRepositoryFake
import com.example.askceny.data.repositories.QuizRepositoryFake
import com.example.askceny.data.repositories.RepositoryProvider
import com.example.askceny.data.types.AuthState
import com.example.askceny.ui.composables.CustomTopBar
import com.example.askceny.ui.composables.EditQuiz
import com.example.askceny.ui.composables.QuizDetail
import com.example.askceny.ui.composables.QuizzesList
import com.example.askceny.ui.composables.SignInScreen
import com.example.askceny.ui.composables.SignUpScreen
import com.example.askceny.ui.theme.AskCenyTheme
import com.example.askceny.ui.viewmodels.AuthViewModel
import com.example.askceny.ui.viewmodels.QuizViewModel

class MainActivity : ComponentActivity() {

    /*
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var auth: FirebaseAuth
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        /*
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        firebaseAnalytics.logEvent("app_start", null)
         */

        val authRepository = RepositoryProvider.authRepository
        val quizRepository = RepositoryProvider.quizRepository

        val viewModelStoreOwner: ViewModelStoreOwner = this
        val authViewModel: AuthViewModel = ViewModelProvider.create(
            viewModelStoreOwner,
            factory = AuthViewModel.Factory,
            extras = MutableCreationExtras().apply {
                set(AuthViewModel.USER_REPOSITORY_KEY, authRepository)
            },
        )[AuthViewModel::class]

        val quizViewModel: QuizViewModel = ViewModelProvider.create(
            viewModelStoreOwner,
            factory = QuizViewModel.Factory,
            extras = MutableCreationExtras().apply {
                set(QuizViewModel.QUIZ_REPOSITORY_KEY, quizRepository)
            },
        )[QuizViewModel::class]

        setContent {
            AskCenyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(context = this, innerPadding = innerPadding, authViewModel = authViewModel, quizViewmodel = quizViewModel)
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    innerPadding: PaddingValues,
    context: Context? = null,
    authViewModel: AuthViewModel,
    quizViewmodel: QuizViewModel
) {

    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: ""

    val authState by authViewModel.authState.collectAsState()
    println("MAIN_SCREEN_VERIFY_AUTHENTICATION:  ${ authState is AuthState.Authenticated }")

    var isAppOnStart : Boolean by rememberSaveable { mutableStateOf(true) }
    var startDestination: String = if (authState is AuthState.Authenticated) "QuizzesList" else "SignIn"

    if (isAppOnStart) {
        if (authState is AuthState.Authenticated) {
            Toast.makeText(context, "Authenticated: Going to $startDestination", Toast.LENGTH_SHORT).show()
        }else {
            Toast.makeText(context, "Unauthenticated: Going to $startDestination", Toast.LENGTH_SHORT).show()
        }
        isAppOnStart = false
    }

    Column(modifier = Modifier.padding(innerPadding)) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {

            when(currentRoute) {
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
                    )
                    println("NAVIGATION_ROUTE: QuizList")
                }
                "QuizDetail" -> {
                    CustomTopBar(
                        modifier = Modifier,
                        showBackButton = true,
                        showEditButton = true,
                        onClickBackPressed = { navController.popBackStack() },
                        onClickEdit = { navController.navigate("EditQuiz") }
                    )
                    println("NAVIGATION_ROUTE: QuizDetail")
                }
                "EditQuiz" -> {
                    CustomTopBar(
                        modifier = Modifier,
                        showBackButton = true,
                        onClickBackPressed = { navController.popBackStack() },
                        onClickSearch = { navController.navigate("Search") }
                    )
                    println("NAVIGATION_ROUTE: QuizEdit")
                }
                "Search" -> {
                    CustomTopBar(
                        modifier = Modifier,
                        showBackButton = true,
                        onClickBackPressed = { navController.popBackStack() }
                    )
                    println("NAVIGATION_ROUTE: QuizEdit")
                }
                else -> { Unit }
            }
        }
        MyNavHost(
            innerPadding = innerPadding,
            navController = navController,
            startDestination = startDestination,
            authViewModel = authViewModel,
            quizViewmodel = quizViewmodel
        )
    }
}

@Composable
fun MyNavHost (
    innerPadding: PaddingValues,
    navController: NavHostController,
    startDestination: String,
    authViewModel: AuthViewModel,
    quizViewmodel: QuizViewModel,
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable("SignIn") {
            SignInScreen(
                modifier = Modifier.padding(innerPadding),
                viewModel = authViewModel,
                signInOnClick = {
                    navController.navigate("QuizzesList") {
                        popUpTo("SignIn") {inclusive = true}
                    }
                },
                signUpOnClick = {navController.navigate("SignUp")}
            )
        }
        composable("SignUp") {
            SignUpScreen (
                modifier = Modifier.padding(innerPadding),
                viewModel = authViewModel,
                signUpOnClick = {
                    navController.navigate("QuizzesList") {
                        popUpTo("SignIn") {inclusive = true}
                    }
                },
                signInOnClick = {
                    navController.popBackStack()
                }
            )
        }
        composable("QuizzesList") {
            QuizzesList(
                viewModel = quizViewmodel,
                modifier = Modifier,
                onClickItem = {navController.navigate("QuizDetail")},
                onClickAddQuiz = { navController.navigate("EditQuiz") }
            )
        }
        composable("QuizDetail") {
            QuizDetail(modifier = Modifier, viewModel = quizViewmodel)
        }
        composable("EditQuiz") { EditQuiz(modifier = Modifier, viewModel = quizViewmodel,
            onBackButton = { navController.popBackStack() })
        } //is also called to create a new quiz
        composable("EditQuestion") { Text("EditQuestion") } // also called to create a new question
        composable("PlayQuiz") { Text("PlayQuiz") }
        composable("Result") { Text("Result") } // called at the end of quiz to show result
        composable("Search") {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Search Test", fontSize = 32.sp) } //
            }
    }
}


@Preview(showBackground = true)
@Composable
fun MainPreview() {
    MainScreen(
        PaddingValues(),
        authViewModel = AuthViewModel(AuthRepositoryFake()),
        quizViewmodel = QuizViewModel(QuizRepositoryFake()))
}