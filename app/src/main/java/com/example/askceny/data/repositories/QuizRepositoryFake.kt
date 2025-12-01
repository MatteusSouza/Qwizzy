package com.example.askceny.data.repositories

import android.util.Log
import com.example.askceny.data.local.MockServerApi
import com.example.askceny.data.local.MockedAuthManager
import com.example.askceny.data.models.Question
import com.example.askceny.data.models.Quiz
import com.example.askceny.data.types.QuestionType
import com.example.askceny.exceptions.AuthException

class QuizRepositoryFake () : QuizRepository {

    private val authManager = MockedAuthManager

    override suspend fun createQuiz(title: String, description: String, img: String, isPublic: Boolean) {
        MockServerApi.createQuiz(title, description = description, img = img, isPublic = isPublic)
    }
    override suspend fun getAllQuizzes(): List<Quiz> {
        try {
            val quizzes = MockServerApi.getAllQuizzes()
            println("QUIZ_REPOSITORY_FAKE: getAllQuizzes.. $quizzes")
            if (quizzes == null)
                return listOf<Quiz>()
            return quizzes
        }catch (e: AuthException) {
            return listOf<Quiz>()
        }
    }
    override suspend fun editQuiz(quizId: String, quizUpdateMap: Map<String, Any>) {
        try {
            MockServerApi.editQuiz(quizId, quizUpdateMap)
        } catch (e: Exception) {
            Log.e("QUIZ_REPOSITORY_EDIT", "Unknown Exception: \n$e")
        }
    }
    override fun deleteQuiz(quizId: String) {
        MockServerApi.deleteQuiz(quizId)
    }
    override fun createQuestion(quizId: String, text: String, type: QuestionType) {
        MockServerApi.createQuestion(quizId = quizId, text = text, type = type)
    }
    override fun getQuizAllQuestions(quizId: String) : MutableList<Question>? {
        return MockServerApi.getQuizAllQuestions(quizId = quizId)
    }
    override fun deleteQuestion(questionId: String ) {
        MockServerApi.deleteQuestion(questionId)
    }
}

/*
Type of quiz

True or False
Can only be one, true or false.
Can be a message at end like: "True, because every doctor and programmers are rich" or "False, because many programmers not have a job"

Multiple Choices
Only one Correct
A)
B) ✅
C)
D)

A quiz can be have questions that answers are differents sizes or same size


interfaces for different type of questions


*/