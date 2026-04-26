package com.example.askceny.data.repositories

import com.example.askceny.domain.models.Question
import com.example.askceny.domain.models.Quiz
import com.example.askceny.domain.repositories.QuizRepository
import com.example.askceny.domain.types.QuestionType

class QuizRepositoryImpl : QuizRepository {
    object QuizList { val it = mutableListOf<Quiz>() }

    override suspend fun createQuiz(title: String, description: String, img: String, isPublic: Boolean) {
        QuizList.it.add(Quiz(id = QuizList.it.size.toString(),description,img))
//        TODO("Not yet implemented")
    }

    override suspend fun getAllQuizzes(): List<Quiz> {
        return QuizList.it.toList()
        TODO("Not yet implemented")
    }

    override suspend fun editQuiz(quizId: String, quizUpdateMap: Map<String, Any>) {
//        val quiz = QuizList.it.find { it.id == quizId }
        return
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
