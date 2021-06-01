package com.example.geoQuiz

import androidx.lifecycle.ViewModel

class QuizViewModel : ViewModel() {
    val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true))

    val correctAnswers = mutableSetOf<Question>()
    var currentIndex = 0
    var isCheater = false

    val currentQuestion: Question
        get() = questionBank[currentIndex]

    fun moveToNext() {
        currentIndex = when (currentIndex) {
            questionBank.size - 1 -> 0
            else -> ++currentIndex
        }
    }

    fun moveToPrev() {
        currentIndex = when(currentIndex) {
            0 -> 0
            else -> --currentIndex
        }
    }
}