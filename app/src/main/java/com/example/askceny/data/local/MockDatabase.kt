package com.example.askceny.data.local

import com.example.askceny.data.types.QuestionType
import com.example.askceny.data.models.Question
import com.example.askceny.data.models.Quiz
import com.example.askceny.data.models.User
import java.util.UUID
import kotlin.collections.forEach
import kotlin.collections.set
import kotlin.random.Random

class MockDatabase {
    var usersCount = 0

    val relationUserQuiz = mutableMapOf<String, MutableList<Quiz>>() /* user_id -> quizzesId[] */
    val relationQuizQuestion = mutableMapOf<String, MutableList<Question>>() /* quiz_xyz_id -> questions_quiz_xyz[] */

    val users = mutableListOf<MockUser>()
    val quizzes = populateQuiz() /*mutableListOf<Quiz>()*/
    val questions = mutableListOf<Question>()

    // User
    fun createUser(email: String, displayName: String, username: String, password: String): User {
        val mockUser = MockUser(
            id = UUID.randomUUID().toString(),
            displayName = displayName,
            username = username,
            email = email,
            about = "",
            website = "",
            password = password
        )
        users.add(mockUser)

        usersCount += 1
        println("createUser: $usersCount users")
        return mockUser.toUser()
    }


    // Quizzes
    fun createQuiz(id: String, title: String, description: String = "", img: String = "", isPublic: Boolean = false) {
        val quiz = Quiz(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            img = img,
            isPublic = isPublic
        )
        quizzes.add(quiz)
        associateUserQuiz(id, quiz)
    }
    fun getAllQuizzes(userId: String) : List<Quiz>? {
        println("MOCK_DATA_BASE: getAllQuizzes.. $relationUserQuiz[userId]")
        return relationUserQuiz[userId]
    }
    fun editQuiz(id: String, quizMap: Map<String, Any>) : Boolean {
        if (id.isEmpty()) {
            println("MOCK_DATA_BASE: EDIT_QUIZ: id can not be empty.")
            return false
        }
        val index = findQuizById(id)
        if (index == null) {
            println("MOCK_DATA_BASE: EDIT_QUIZ: editQuiz id not found")
            return false
        }

        val quiz = quizzes[index]
        println("MOCK_DATA_BASE: EDIT_QUIZ: Quiz title; ${quiz.title}")

        quizMap.forEach { (key, value) ->
            when (key) {
                "title" -> quiz.title = value as String
                "description" -> quiz.description = value as String
                "img" -> quiz.img = value as String
                "isPublic" -> quiz.isPublic = value as Boolean
            }
        }
        return true
    }
    fun deleteQuiz(userId: String, quizId: String) {
        //TODO: To implement
        /*
       relationUserQuiz.forEach { index, quiz ->
           println("delete-- \nUserId: $index\nQuiz List: $quiz\ndelete--")
       } */
    }


    // Question
    fun createQuestion(quizId: String, text: String, type: QuestionType) {
        val question = Question(id= UUID.randomUUID().toString() ,text = text, type = type)
        questions.add(question)
        associateQuizQuestion(quizId, question)
    }
    fun getQuizAllQuestions(quizId: String) : MutableList<Question>? {
        return relationQuizQuestion[quizId]
    }
    fun deleteQuestion(questionId: String ) {
        //TODO: To implement
        println("\nDetecting Question in the relation")
        relationQuizQuestion.forEach { (key, value) ->
            println("before: $key .. $value")
            value.removeIf {it.id == questionId}
            println("after: $key .. $value")
        }
        println("\n\nQuizzes: $quizzes \nQuestions: $questions \nRelationUserQuiz $relationUserQuiz \nRelationQuizQuestions $relationQuizQuestion")
    }


    // Search
    fun findIndexUserById(id: String): Int? {
        users.forEachIndexed { index, user ->
            if (id == user.id.toString()) return index
        }
        return null
    }
    fun findIndexUserByEmail(email: String): Int? {
        users.forEachIndexed { index, user ->
            if (email == user.email) return index
        }
        return null
    }
    fun findIndexUserByUsername(username: String): Int? {
        users.forEachIndexed { index, user ->
            if (username == user.username) return index
        }
        return null
    }
    fun emailExists(email: String): Boolean {
        users.forEach { user ->
            if (email == user.email) return true
        }
        return false
    }
    fun usernameExists(username: String): Boolean {
        users.forEach { user ->
            if (username == user.username) return true
        }
        return false
    }


    //Quizzes
    fun findQuizById(id: String): Int? {
        quizzes.forEachIndexed { index, quiz ->
            if (id == quiz.id.toString()) return index
        }
        return null
    }

    // Relations
    private fun associateUserQuiz(userId: String, quiz: Quiz) {
        if (relationUserQuiz.containsKey(userId)) {
            relationUserQuiz[userId]?.add(quiz)
        } else {
            relationUserQuiz[userId] = mutableListOf(quiz)
        }
    }
    private fun associateQuizQuestion(quizId: String, question: Question) {
        if (relationQuizQuestion.containsKey(quizId)) {
            relationQuizQuestion[quizId]?.add(question)
        } else {
            relationQuizQuestion[quizId] = mutableListOf(question)
        }
    }

    private fun populateQuiz() : MutableList<Quiz> {
        val list = mutableListOf<Quiz>()
        val user = createUser(
            email = "test@test.com",
            displayName = "Test User",
            username = "TestUser",
            password = "test123"
        )

        val fakeDescription =
            "• Lorem ipsum dolor sit amet, consectetur adipiscing elit.\n" +
                    "• Fusce efficitur tellus et blandit commodo.\n" +
                    "• Pellentesque eget risus at leo dapibus ultrices."

        for (i in 1 .. 20 /*60000*/){
            val quiz = Quiz(
                id = UUID.randomUUID().toString(),
                title = "Quiz $i",
                description = fakeDescription,
                img = "https://picsum.photos/id/${Random.nextInt(1, 1000)}/600/400",
                isPublic = false
            )
            list.add(quiz)
            associateUserQuiz(user.id, quiz)
        }
        println("MockDataBase.PopulateList: QUIZZES_SIZE ${list.size}")
        return list
    }
}