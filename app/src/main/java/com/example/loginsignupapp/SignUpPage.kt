package com.example.loginsignupapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.loginsignupapp.databinding.ActivitySignUpPageBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpPage : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpPageBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        auth = FirebaseAuth.getInstance()
        binding = ActivitySignUpPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //On click listener for back button
        binding.button.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        //On click listener for register button
        binding.button2.setOnClickListener {
            val email = binding.editText1.text.toString()
            val userName = binding.editText2.text.toString()
            val password = binding.editText3.text.toString()
            val confirmPassword = binding.editText4.text.toString()

            if (email.isEmpty() || userName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please Fill All The Details ", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Repeat Password Must Be Same As Password", Toast.LENGTH_SHORT)
                    .show()
            } else {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Registration Successfull", Toast.LENGTH_SHORT)
                                .show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Registration Failed : ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }
            }


        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}