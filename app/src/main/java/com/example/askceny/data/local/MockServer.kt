package com.example.askceny.data.local

import com.example.askceny.data.types.QuestionType
import com.example.askceny.data.models.Question
import com.example.askceny.data.models.Quiz
import com.example.askceny.data.models.User
import com.example.askceny.data.types.ErrorCode
import com.example.askceny.exceptions.AuthException
import java.util.UUID

class MockServer(val database: MockDatabase) {
    val activeSessions = mutableMapOf<String, String>() // token -> id
    var failedLoginAttempts = 0

    fun createUser(email: String, displayName: String, username: String, password: String): String? {
        if (!isEmail(email))
            throw AuthException(ErrorCode.INVALID_EMAIL)
        val index = database.findIndexUserByEmail(email)
        if (index != null)
            throw AuthException(ErrorCode.EMAIL_ALREADY_IN_USE)

        val user = database.createUser(email = email, displayName = displayName, username = username, password = password)
        val token = UUID.randomUUID().toString()
        activeSessions[token] = user.id.toString()
        return token
    }

    fun getUser(token: String?): User? {
        if (token == null)
            return null

        val id = activeSessions[token]
        if (id == null)
            return null

        val index = database.findIndexUserById(id)
        if (index == null)
            return null

        return database.users[index].toUser()
    }

    fun getUserByUsername(username: String, token: String?): User? {
        if (token == null)
            return null
        if (!isActiveToken(token))
            return null
        val index = (database.findIndexUserByUsername(username))
        if (index == null)
            return null
        return database.users[index].toUser()
    }

    fun isEmail(email: String): Boolean {
        return email.isNotEmpty() && email.length > 4 && email.contains("@") && email.contains(".")
    }

    fun login(email: String, password: String): String? {
        if (!isEmail(email))
            throw AuthException(ErrorCode.INVALID_EMAIL)
        val user = userValidate(email, password)
        val token = UUID.randomUUID().toString()
        activeSessions[token] = user.id.toString()
        return token
    }

    fun logoff(token: String): Boolean {
        return activeSessions.remove(token) != null
    }

    fun isAuthenticated(token: String?): Boolean {
        if (token == null) {
            return false
        }
        return activeSessions.containsKey(token)
    }

    fun createQuiz(title: String, description: String, img: String, isPublic: Boolean, token: String?) {
        if (token == null)
            return
        val user = getUser(token)
        if (user!= null)
            database.createQuiz(id = user.id, title = title, description = description, img = img, isPublic = isPublic)
    }

    fun createQuestion(token: String, quizId: String, text: String, type: QuestionType) {
        if (isActiveToken(token)) {
            database.createQuestion(quizId, text, type)
        }
    }

    fun getQuizAllQuestions(token: String, quizId: String) : MutableList<Question>? {
        if (isActiveToken(token)){
            return database.getQuizAllQuestions(quizId)
        }
        return null
    }

    fun editQuiz(token: String, quizId: String, quizUpdateMap: Map<String, Any>) {
        val user = getUser(token)
        if (isActiveToken(token))
            database.editQuiz(quizId, quizUpdateMap)
    }

    fun deleteQuiz(token: String, quizId: String) {
        val user = getUser(token)
        if (user!= null)
            database.deleteQuiz(user.id, quizId)
    }

    fun deleteQuestion(token: String, questionId: String ) {
        if (isActiveToken(token))
            database.deleteQuestion(questionId)
    }


    fun getAllQuizzes(token: String) : List<Quiz>? {
        val user = getUser(token)
        if (user!= null)
            return database.getAllQuizzes(user.id)
        return null
    }

    private fun isActiveToken(token: String?): Boolean {
        val token = activeSessions[token]
        return token != null
    }

    private fun userValidate(email: String, password: String): User {
        val index = database.findIndexUserByEmail(email)
        if (index == null){
            throw AuthException(ErrorCode.ERROR_INVALID_CREDENTIAL)
        }
        val mockUser = database.users[index]
        if (password != mockUser.password) {
            throw AuthException(ErrorCode.ERROR_INVALID_CREDENTIAL)
        }
        return mockUser.toUser()
    }

    private fun validateSignUp(email: String, username: String, password: String) {
        require(isEmail(email) == true) { "Invalid E-mail" }
        require(email.isNotEmpty()) { "The email cannot be empty" }
        require(database.emailExists(email) == false) { "This email has already in use" }

        require(username.isNotEmpty()) { "The username field cannot be empty" }
        require(username.length >= 4) { "The username should be 4 or more characters" }
        require(database.usernameExists(username) == false) { "This username has already in use" }

        require(password.isNotEmpty()) { "Password cannot be empty" }
        require(password.length >= 7) { "Password should be 7 or more characters" }
    }
}