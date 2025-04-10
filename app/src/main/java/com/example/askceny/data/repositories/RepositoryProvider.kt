package com.example.askceny.data.repositories

object RepositoryProvider {
    // Switch between fake and real API here.
    private val container = FakeAppContainer()

    val authRepository = container.authRepository
    val quizRepository = container.quizRepository

}

private class AppContainer() {
//    val authRepository by lazy { AuthRepositoryImpl() }
//    val authRepository by lazy { QuizRepositoryImpl() }
}
private class FakeAppContainer() {
    val authRepository by lazy { AuthRepositoryFake() }
    val quizRepository by lazy { QuizRepositoryFake() }
}
