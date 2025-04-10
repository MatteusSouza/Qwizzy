package com.example.askceny.data.repositories

import com.example.askceny.data.models.Question
import com.example.askceny.data.models.Quiz
import com.example.askceny.data.types.QuestionType

interface QuizRepository {

    fun createQuiz(title: String, description: String = "", img: String = "",isPublic: Boolean = false)
    fun getAllQuizzes(): List<Quiz>
    fun editQuiz(id: String, title: String, description: String, img: String, isPublic: Boolean)
    fun deleteQuiz(quizId: String)
    fun createQuestion(quizId: String, text: String, type: QuestionType)
    fun getQuizAllQuestions(quizId: String) : MutableList<Question>?
    fun deleteQuestion(questionId: String )
}