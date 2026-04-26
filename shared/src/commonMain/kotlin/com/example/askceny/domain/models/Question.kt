package com.example.askceny.domain.models

import com.example.askceny.domain.types.QuestionType

data class Question(
    val id: String,
    var text: String,
    val type: QuestionType,
    val answers: MutableList<Answer> = mutableListOf<Answer>(),
    val correctAnswer: MutableList<Int> = mutableListOf<Int>()
)
{
    fun addAnswer(
        text: String,
        img: String = ""
    ) {
        answers.add(Answer(text,img))
    }

    fun removeAnswer(index: Int) {
        answers.removeAt(index)
    }

    fun editAnswer(index: Int, text: String, img: String) {
        answers[index].text = text
        answers[index].img = img
    }

}
