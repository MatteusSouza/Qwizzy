package com.example.askceny.data.repositories

import com.example.askceny.data.models.Question
import com.example.askceny.data.models.Quiz
import com.example.askceny.data.types.QuestionType

class QuizRepositoryImpl: QuizRepository {
    override fun createQuiz(
        title: String,
        description: String,
        img: String,
        isPublic: Boolean
    ) {
//        TODO("Not yet implemented"
        null
    }

    override fun getAllQuizzes(): List<Quiz> {
//        TODO("Not yet implemented")
        return listOf()
    }

    override fun editQuiz(
        id: String,
        title: String,
        description: String,
        img: String,
        isPublic: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override fun deleteQuiz(quizId: String) {
        TODO("Not yet implemented")
    }

    override fun createQuestion(
        quizId: String,
        text: String,
        type: QuestionType
    ) {
        TODO("Not yet implemented")
    }

    override fun getQuizAllQuestions(quizId: String): MutableList<Question>? {
        TODO("Not yet implemented")
    }

    override fun deleteQuestion(questionId: String) {
        TODO("Not yet implemented")
    }

}