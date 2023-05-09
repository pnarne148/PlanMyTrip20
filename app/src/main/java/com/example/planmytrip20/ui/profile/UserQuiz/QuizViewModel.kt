package com.example.planmytrip20.ui.profile.UserQuiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

private const val CURRENT_INDEX_KEY = "CURRENT_INDEX_KEY"

class QuizViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val questionBank = listOf(
        Question(
            question = "What kind of traveler are you?",
            options = listOf(
                "Adventure",
                "Luxury",
                "Budget",
                "Family"
            ),
            selectedOption = -1
        ),
        Question(
            question = "Which type of climate do you prefer?",
            options = listOf(
                "Warm",
                "Cool",
                "Cold",
                "Humid"
            ),
            selectedOption = -1
        ),
        Question(
            question = "What is your preferred mode of transportation?",
            options = listOf(
                "Flight",
                "Train",
                "Bus",
                "Car"
            ),
            selectedOption = -1
        ),
        Question(
            question = "What type of accommodation do you prefer?",
            options = listOf(
                "Hotel",
                "Hostel",
                "Airbnb",
                "Resort"
            ),
            selectedOption = -1
        ),
        Question(
            question = "What is your preferred type of destination?",
            options = listOf(
                "Beach",
                "City",
                "Countryside",
                "Mountain"
            ),
            selectedOption = -1
        ),
        Question(
            question = "What is your budget per day?",
            options = listOf(
                "< $50",
                "$50 - $100",
                "$100 - $200",
                "> $200"
            ),
            selectedOption = -1
        ),
        Question(
            question = "What is your preferred time of travel?",
            options = listOf(
                "Peak season",
                "Off-season",
                "Shoulder season",
                "Anytime"
            ),
            selectedOption = -1
        )
    )

    private var currentIndex: Int
        get() = savedStateHandle.get(CURRENT_INDEX_KEY) ?: 0
        set(value) = savedStateHandle.set(CURRENT_INDEX_KEY, value)

    val currentQuestionIndex: Int
        get() = currentIndex

    val questionCount: Int
        get() = questionBank.size

    val currentQuestionAnswer: Int
        get() = questionBank[currentIndex].selectedOption
    val currentQuestionText: String
        get() = questionBank[currentIndex].question
    val currentQuestionOptions: List<String>
        get() = questionBank[currentIndex].options

    fun getQuestionsAndAnswers(): List<Pair<String, String>> {
        return questionBank.map { it.question to it.options[it.selectedOption] }
    }
    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun moveToPrevious() {
        if (currentIndex != 0) {
            currentIndex = (currentIndex - 1) % questionBank.size
        }
    }

    fun updateAnswer(answerIndex: Int) {
        questionBank[currentIndex].selectedOption = answerIndex
    }

    fun isLastQuestion(): Boolean {
        return currentIndex == questionBank.size - 1
    }
}