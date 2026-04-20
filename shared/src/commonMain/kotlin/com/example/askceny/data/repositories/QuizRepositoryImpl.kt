package com.example.askceny.data.repositories

import com.example.askceny.domain.models.Question
import com.example.askceny.domain.models.Quiz
import com.example.askceny.domain.repositories.QuizRepository
import com.example.askceny.domain.types.QuestionType

class QuizRepositoryImpl : QuizRepository {
    override suspend fun createQuiz(title: String, description: String, img: String, isPublic: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllQuizzes(): List<Quiz> {
        TODO("Not yet implemented")
    }

    override suspend fun editQuiz(quizId: String, quizUpdateMap: Map<String, Any>) {
        TODO("Not yet implemented")
    }

    override fun deleteQuiz(quizId: String) {
        TODO("Not yet implemented")
    }

    override fun createQuestion(quizId: String, text: String, type: QuestionType) {
        TODO("Not yet implemented")
    }

    override fun getQuizAllQuestions(quizId: String): MutableList<Question>? {
        TODO("Not yet implemented")
    }

    override fun deleteQuestion(questionId: String) {
        TODO("Not yet implemented")
    }
}
