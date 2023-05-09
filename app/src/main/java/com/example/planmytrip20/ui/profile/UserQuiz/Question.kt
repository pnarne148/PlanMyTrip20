package com.example.planmytrip20.ui.profile.UserQuiz

data class Question(
    val question: String,
    val options: List<String>,
    var selectedOption: Int
)