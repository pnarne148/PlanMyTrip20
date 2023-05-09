package com.example.planmytrip20.ui.profile.UserQuiz

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import com.example.planmytrip.auth.LoginActivity
import com.example.planmytrip20.R
import com.example.planmytrip20.databinding.ActivityUserpreferencesBinding
import com.google.firebase.firestore.FirebaseFirestore


class UserPreferencesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserpreferencesBinding
    private val quizViewModel: QuizViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserpreferencesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        updateQuestion()

        binding.option1Button.setOnClickListener {
            updateAnswerAndMoveToNext(0)
        }

        binding.option2Button.setOnClickListener {
            updateAnswerAndMoveToNext(1)
        }

        binding.option3Button.setOnClickListener {
            updateAnswerAndMoveToNext(2)
        }

        binding.option4Button.setOnClickListener {
            updateAnswerAndMoveToNext(3)
        }

        binding.skipButton.setOnClickListener {
            if (quizViewModel.isLastQuestion()) {
                storeAnswersInFirebase()
                val loginIntent = Intent(this, LoginActivity::class.java)
                startActivity(loginIntent)
                finish()
            } else {
                quizViewModel.moveToNext()
                updateQuestion()
            }
        }
    }

    private fun updateQuestion() {
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)

        val questionText = quizViewModel.currentQuestionText
        val options = quizViewModel.currentQuestionOptions
        val currentQuestionNumber = quizViewModel.currentQuestionIndex + 1
        val totalQuestions = quizViewModel.questionCount

        binding.questionTextView.text = questionText
        binding.questionTextView.startAnimation(fadeIn)

        binding.optionsContainer.startAnimation(fadeOut)
        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                binding.option1Button.text = options[0]
                binding.option2Button.text = options[1]
                binding.option3Button.text = options[2]
                binding.option4Button.text = options[3]
                binding.optionsContainer.startAnimation(fadeIn)
            }
        })

        binding.questionNumberTextView.text = "Question $currentQuestionNumber/$totalQuestions"
    }

    private fun updateAnswerAndMoveToNext(userAnswerIndex: Int) {
        quizViewModel.updateAnswer(userAnswerIndex)

        if (quizViewModel.isLastQuestion()) {
            // Store answers in Firebase
            storeAnswersInFirebase()
            //val returnIntent = Intent()
            //setResult(Activity.RESULT_OK, returnIntent)
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
            finish()
        } else {
            quizViewModel.moveToNext()
            updateQuestion()
        }
    }

    private fun storeAnswersInFirebase() {
        val answers = hashMapOf<String, String>()
        for (pair in quizViewModel.getQuestionsAndAnswers()) {
            answers[pair.first] = pair.second
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("userPreferences")
                .document(userId)
                .set(answers)
                .addOnSuccessListener {
                    Toast.makeText(this, "User Preferences Stored", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to store user preferences", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show()
        }
    }


}
