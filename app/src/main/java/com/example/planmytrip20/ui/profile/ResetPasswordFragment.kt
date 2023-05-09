package com.example.planmytrip20.ui.profile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.planmytrip.auth.LoginActivity
import com.example.planmytrip20.databinding.FragmentResetPasswordBinding

import com.google.firebase.auth.FirebaseAuth

class ResetPasswordFragment : Fragment() {
    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        val root: View = binding.root
        root.setBackgroundColor(Color.WHITE);


        firebaseAuth = FirebaseAuth.getInstance()
        val email = firebaseAuth.currentUser?.email
        binding.resetButton.setOnClickListener {
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()
            if ( pass.isNotEmpty() && confirmPass.isNotEmpty() && email!=null) {
                if (pass == confirmPass) {
                    firebaseAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Password reset email sent successfully
                                Toast.makeText(requireContext(), "Password reset email sent.", Toast.LENGTH_SHORT).show()
                            } else {
                                // Password reset email failed to send
                                Toast.makeText(requireContext(), "Failed to send password reset email.", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(requireContext(), "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()

            }

    }
        return root
}
    }