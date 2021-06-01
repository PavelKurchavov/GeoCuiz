package com.example.geoQuiz

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val REQUEST_CODE_CHEAT = 0
private const val TRUE_BTN_ENABLED = "trueBtnEnabled"
private const val FALSE_BTN_ENABLED = "falseBtnEnabled"


class MainActivity : AppCompatActivity() {
    private val quizViewModel : QuizViewModel by lazy { ViewModelProvider(this).get(QuizViewModel::class.java) }
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var prevButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var questionTW: TextView
    private lateinit var cheatButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        quizViewModel.currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0

        trueButton = findViewById(R.id.true_btn)
        falseButton = findViewById(R.id.false_btn)
        prevButton = findViewById(R.id.prev_btn)
        nextButton = findViewById(R.id.next_btn)
        cheatButton = findViewById(R.id.cheat_btn)
        questionTW = findViewById(R.id.question_text_view)
        questionTW.setText(quizViewModel.currentQuestion.textResId)

        trueButton.isEnabled = savedInstanceState?.getBoolean(TRUE_BTN_ENABLED) ?: true
        falseButton.isEnabled = savedInstanceState?.getBoolean(FALSE_BTN_ENABLED) ?: true

        val changeQuestionListener = View.OnClickListener {
            with(quizViewModel) {
                when(it.id) {
                    R.id.prev_btn -> {
                        moveToPrev()
                        questionTW.setText(currentQuestion.textResId)
                    }
                    R.id.next_btn ->  {
                        if (currentIndex == questionBank.size - 1) showToast("Correct answers: ${(correctAnswers.size.toDouble() / questionBank.size * 100).toInt()}%")
                        moveToNext()
                        questionTW.setText(currentQuestion.textResId)
                        enableTrueAndFalseButtons()
                    }
                }
            }
        }

        prevButton.setOnClickListener(changeQuestionListener)
        nextButton.setOnClickListener(changeQuestionListener)
        trueButton.setOnClickListener {
            checkAnswer(quizViewModel.currentQuestion, true)
            disableTrueAndFalseButtons()
        }
        falseButton.setOnClickListener {
            checkAnswer(quizViewModel.currentQuestion, false)
            disableTrueAndFalseButtons()
        }
        cheatButton.setOnClickListener {
            val intent = CheatActivity.newIntent(this@MainActivity, quizViewModel.currentQuestion.answer)
            startActivityForResult(intent, REQUEST_CODE_CHEAT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        if (requestCode == REQUEST_CODE_CHEAT) {
            quizViewModel.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }

    private fun enableTrueAndFalseButtons() {
        trueButton.isEnabled = true
        falseButton.isEnabled = true
    }

    private fun disableTrueAndFalseButtons() {
        trueButton.isEnabled = false
        falseButton.isEnabled = false
    }

    private fun checkAnswer(question: Question, userAnswer: Boolean) {
        var messageResId = 0
        when {
            quizViewModel.isCheater -> messageResId = R.string.judgment_toast
            userAnswer == question.answer -> {
                messageResId = R.string.correct_answer_toast
                quizViewModel.correctAnswers += question
            }
            else -> messageResId = R.string.incorrect_answer_toast
        }
       showToast(getText(messageResId).toString())
    }

    private fun showToast(message: String) {
         val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
         toast.setGravity(Gravity.TOP, 0, 128)
         toast.show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_INDEX, quizViewModel.currentIndex)
        outState.putBoolean(TRUE_BTN_ENABLED, trueButton.isEnabled)
        outState.putBoolean(FALSE_BTN_ENABLED, falseButton.isEnabled)
    }
}