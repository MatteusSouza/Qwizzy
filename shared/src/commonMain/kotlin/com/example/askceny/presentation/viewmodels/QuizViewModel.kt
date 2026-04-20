package com.example.askceny.presentation.viewmodels

import com.example.askceny.domain.models.Quiz
import com.example.askceny.domain.repositories.QuizRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QuizViewModel(private val quizRepository: QuizRepository) : ViewModel() {
    private val _quizzesState : MutableStateFlow<MutableList<Quiz>> = MutableStateFlow(mutableListOf())
    private val _quizInFocus = MutableStateFlow<Quiz?>(null)

    val quizzesState : StateFlow<List<Quiz>> = _quizzesState
    val quizInFocus: StateFlow<Quiz?> = _quizInFocus

    init {
        viewModelScope.launch {
            println("QUIZ_VIEW_MODEL: QuizViewModel Start")
        }
    }

    fun saveQuiz(title: String, description: String) {
        println("QUIZ_VIEW_MODEL: SAVE_QUIZ: editQuiz called")
        viewModelScope.launch {
            val currentQuiz = getQuizInFocus()
            println("QuizzesViewModel.saveQuiz: onFocus ${getQuizInFocus()}")
            if (currentQuiz == null){
                    quizRepository.createQuiz(title = title, description = description)
                    println("QUIZ_VIEW_MODEL: SAVE_QUIZ: createQuiz called")
            }
            else {
                val quizUpdateMap = mutableMapOf<String, Any>()
                val quizUpdateState = quizInFocus.value

                if (title != currentQuiz.title) {
                    quizUpdateMap["title"] = title
                    quizUpdateState?.title = title
                }
                if (description != currentQuiz.description) {
                    quizUpdateMap["description"] = description
                    quizUpdateState?.description = description
                }
                if (quizUpdateMap.isNotEmpty()) {
                    quizRepository.editQuiz(currentQuiz.id, quizUpdateMap)
                    _quizInFocus.value = quizUpdateState
                }
            }
                _quizzesState.value = quizRepository.getAllQuizzes().toMutableList()
        }
    }

    suspend fun getAllQuizzes(): List<Quiz> {
        return quizRepository.getAllQuizzes()
    }

    fun getQuizInFocus() : Quiz? {
        println("QUIZ_DETAIL: QUIZ_VIEW_MODEL: Is QuizInFocus Null: ${quizInFocus.value == null}")
        return quizInFocus.value
    }

    fun setQuizInFocus(quiz: Quiz?) {
        _quizInFocus.value = quiz
        println("QUIZ_DETAIL: QUIZ_VIEW_MODEL: Is quizInFocus null: ${quizInFocus.value == null}")
    }

    fun update() {
        viewModelScope.launch {
            println("QuizViewModel: Update called")
            _quizzesState.value = quizRepository.getAllQuizzes().toMutableList()
        }
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