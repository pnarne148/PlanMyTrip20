package com.example.planmytrip.auth

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.planmytrip20.classes.database
import com.example.planmytrip20.databinding.ActivitySignUpBinding

import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val username = binding.nameEt.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {

                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {

                            val uid= firebaseAuth.currentUser?.uid
                            val userDetails = hashMapOf(
                                "userName" to username,
                                "email" to email,
                                "uid" to uid
                            )
                            if (uid != null) {
                                database.db.collection("userDetails").document(uid).set(userDetails)
                                    .addOnSuccessListener {
                                        Log.d(ContentValues.TAG, "Users details successfully updated")
                                        val intent = Intent(this, LoginActivity::class.java)
                                        startActivity(intent)
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.w(ContentValues.TAG, "Error saving user details.", exception)
                                    }
                            }
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

                        }
                    }

                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()

            }
        }
    }
}