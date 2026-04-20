package com.example.askceny.domain.repositories

import com.example.askceny.domain.models.Question
import com.example.askceny.domain.models.Quiz
import com.example.askceny.domain.types.QuestionType

interface QuizRepository {

    suspend fun createQuiz(title: String, description: String = "", img: String = "", isPublic: Boolean = false)
    suspend fun getAllQuizzes(): List<Quiz>
    suspend fun editQuiz(quizId: String, quizUpdateMap: Map<String, Any>)
    fun deleteQuiz(quizId: String)
    fun createQuestion(quizId: String, text: String, type: QuestionType)
    fun getQuizAllQuestions(quizId: String) : MutableList<Question>?
    fun deleteQuestion(questionId: String )
}
