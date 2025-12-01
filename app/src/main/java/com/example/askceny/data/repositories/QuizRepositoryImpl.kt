package com.example.askceny.data.repositories

import android.util.Log
import com.example.askceny.data.models.Question
import com.example.askceny.data.models.Quiz
import com.example.askceny.data.types.QuestionType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await

class QuizRepositoryImpl: QuizRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override suspend fun createQuiz(
        title: String,
        description: String,
        img: String,
        isPublic: Boolean
    ) {
        val newQuiz = Quiz("", title, description, img, isPublic)
        try {
            val userId = auth.currentUser?.uid
            Log.d("QUIZ_REPOSITORY_CREATE", "userId: $userId:")
            if (userId != null) {
                val quizRef = db.collection("users")
                    .document(userId)
                    .collection("quizzes")
                    .add(newQuiz)
                Log.d("QUIZ_REPOSITORY_CREATE", "quizRef $quizRef")
            }
        } catch (e: Exception) {
            Log.e("QUIZ_REPOSITORY_CREATE", "Unknown Exception on createQuiz:\n $e")
        }
    }

    override suspend fun getAllQuizzes(): List<Quiz> {
//        TODO("Not yet implemented")
        try {
            val userId = auth.currentUser?.uid
            Log.d("QUIZ_REPOSITORY", "userId $userId")
            if (userId != null) {
                val quizzesRef = db.collection("users")
                    .document(userId).collection("quizzes")
                Log.d("QUIZ_REPOSITORY", "quizzesRef $quizzesRef")
                val snapshot = quizzesRef.get().await()
                Log.d("QUIZ_REPOSITORY", "snapshot is empty: ${snapshot.isEmpty}")
                val quizList: List<Quiz> = snapshot.documents
                    .mapNotNull { document ->
                        document.toObject(Quiz::class.java)?.copy(id = document.id);
                    }.toMutableList()
                Log.d("QUIZ_REPOSITORY", "quizList is empty: ${quizList.isEmpty()}")
                return quizList
            }
            Log.e("QUIZ_REPOSITORY", "UserId returns null on getAllQuizzes")
            return listOf() // todo: implement an error
        } catch (e: FirebaseFirestoreException) {
            Log.e("QUIZ_REPOSITORY", "FirebaseFirestoreException on getAllQuizzes:\n $e")
            return listOf()
        }
        catch (e: Exception) {
            Log.e("QUIZ_REPOSITORY", "Unknown Exception on getAllQuizzes:\n $e")
            return listOf()
        }
    }

    override suspend fun editQuiz(id: String, quizUpdateMap: Map<String, Any>) {
        try {
            val userId = auth.currentUser?.uid
            Log.d("QUIZ_REPOSITORY_EDIT", "userId: $userId:")
            if (userId != null) {
//                val newQuiz = Quiz("", title, description, img, isPublic)

                val quizRef = db.collection("users")
                    .document(userId)
                    .collection("quizzes")
                    .document(id)
                    .update(quizUpdateMap)
                    .await()
                Log.d("QUIZ_REPOSITORY_EDIT", "quizRef $quizRef")
            }
        } catch (e: Exception) {
            Log.e("QUIZ_REPOSITORY_EDIT", "Unknown Exception on editQuiz:\n $e")
        }
    }

    suspend fun editQuiz2(
        id: String,
        title: String,
        description: String,
        img: String,
        isPublic: Boolean
    ) {
//        TODO("Not yet implemented")
        try {
            val userId = auth.currentUser?.uid
            Log.d("QUIZ_REPOSITORY_EDIT", "userId: $userId:")
            if (userId != null) {
//                val newQuiz = Quiz("", title, description, img, isPublic)

                val updates = mapOf("title" to "Update .. ENEM 2025 - Simulado de Ciências da Natureza e suas Tecnologias")

                val quizRef = db.collection("users")
                    .document(userId)
                    .collection("quizzes")
                    .document(id)
                    .update(updates)
                    .await()
                Log.d("QUIZ_REPOSITORY_EDIT", "quizRef $quizRef")
            }
        } catch (e: Exception) {
            Log.e("QUIZ_REPOSITORY_EDIT", "Unknown Exception on editQuiz:\n $e")
        }
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