package com.example.askceny.data.local

import com.example.askceny.data.types.QuestionType
import com.example.askceny.data.models.Question
import com.example.askceny.data.models.Quiz
import com.example.askceny.data.models.User
import com.example.askceny.data.types.ErrorCode
import com.example.askceny.exceptions.AuthException

abstract class MockServerApi {

    companion object {

        private val database = MockDatabase()
        private val server = MockServer(database)

        //User
        fun createUser(email: String, displayName: String, username: String, password: String) : String? {
            return server.createUser(email, displayName, username, password)
        }
        fun getUser(): User? {
            val token = tokenIfLoggedIn()
            return server.getUser(token)
        }

        //Search
        fun getUserByUsername(username: String) : User? {
            val token = tokenIfLoggedIn()
            return server.getUserByUsername(username, token)
        }

        //Validate Login
        fun isEmail(email: String): Boolean {
            return server.isEmail(email)
        }
        fun emailExists(email: String): Boolean {
            return database.emailExists(email)
        }
        fun usernameExists(username: String): Boolean {
            return database.usernameExists(username)
        }

        //Quizzes
        fun createQuiz(title: String, description: String, img: String, isPublic: Boolean) {
            val token = tokenIfLoggedIn()
            return server.createQuiz(
                title = title,
                description = description,
                img = img,
                isPublic = isPublic,
                token = token
            )
        }
        fun getAllQuizzes() : List<Quiz>? {
            val token = tokenIfLoggedIn()
            return server.getAllQuizzes(token)
        }
        fun editQuiz(
            id: String,
            title: String,
            description: String,
            img: String,
            isPublic: Boolean
        ) {
            val token = tokenIfLoggedIn()
            server.editQuiz(token = token, id = id, title = title, description = description,img = img, isPublic = isPublic)
        }
        fun deleteQuiz(quizId: String) {
            val token = tokenIfLoggedIn()
            return server.deleteQuiz(token, quizId)
        }

        //Questions
        fun createQuestion(quizId: String, text: String, type: QuestionType) {
            val token = tokenIfLoggedIn()
            server.createQuestion(token,quizId, text, type)
        }
        fun getQuizAllQuestions(quizId: String) : MutableList<Question>? {
            val token = tokenIfLoggedIn()
            return server.getQuizAllQuestions(token, quizId)
        }
        fun deleteQuestion(questionId: String ) {
            val token = tokenIfLoggedIn()
            server.deleteQuestion(token, questionId)
        }

        //Auth
        fun login(email: String, password: String): String? {
            return server.login(email, password)
        }
        fun logoff(token: String) : Boolean {
            return server.logoff(token)
        }
        fun isAuthenticated(): Boolean {
            val token = tokenIfLoggedIn()
            return server.isAuthenticated(token)
        }
        private fun tokenIfLoggedIn(): String {
            val token = MockedAuthManager.getToken()
            if (token != null)
                return token
            throw AuthException(ErrorCode.UNAUTHENTICATED)
        }
    }
}