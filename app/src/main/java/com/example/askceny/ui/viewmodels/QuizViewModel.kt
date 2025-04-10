package com.example.askceny.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.askceny.data.models.Quiz
import com.example.askceny.data.repositories.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QuizViewModel(private val quizRepository: QuizRepository) : ViewModel() {
    private val _quizzesState : MutableStateFlow<MutableList<Quiz>> = MutableStateFlow(mutableListOf())
    private var quizInFocus : Quiz? = null

    val quizzesState : StateFlow<List<Quiz>> = _quizzesState

    init {
        viewModelScope.launch {
            update()
            println("QUIZ_VIEW_MODEL: QUIZZES_SIZE ${quizzesState.value.size}")
        }
    }

    fun saveQuiz(title: String, description: String) {
        viewModelScope.launch {
            val quiz = getQuizInFocus()
            println("QuizzesViewModel.saveQuiz: onFocus ${getQuizInFocus()}")
            if (quiz == null){
                    quizRepository.createQuiz(title = title, description = description)
                    println("QUIZ_VIEW_MODEL: SAVE_QUIZ: createQuiz called")
            }
            else {
                quizRepository.editQuiz(
                    id = quiz.id,
                    title = title,
                    description = description,
                    img = quiz.img,
                    isPublic = quiz.isPublic
                )
                println("QUIZ_VIEW_MODEL: SAVE_QUIZ: editQuiz called")

            }
            _quizzesState.value = quizRepository.getAllQuizzes().toMutableList()
        }
    }

    fun getAllQuizzes(): List<Quiz> {
        return quizRepository.getAllQuizzes()
    }

    fun getQuizInFocus() : Quiz? {
        println("QUIZ_DETAIL: QUIZ_VIEW_MODEL: Is QuizInFocus Null: ${quizInFocus == null}")
        return quizInFocus
    }

    fun setQuizInFocus(quiz: Quiz?) {
        quizInFocus = quiz
        println("QUIZ_DETAIL: QUIZ_VIEW_MODEL: Is quizInFocus null: ${quizInFocus == null}")
    }

    fun update() {
        println("QuizViewModel: Update called")
        _quizzesState.value = quizRepository.getAllQuizzes().toMutableList()
    }

    companion object {
        val QUIZ_REPOSITORY_KEY = object : CreationExtras.Key<QuizRepository> {}
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val quizRepository = this[QUIZ_REPOSITORY_KEY] as QuizRepository
                QuizViewModel(
                    quizRepository = quizRepository
                )
            }
        }
    }
}