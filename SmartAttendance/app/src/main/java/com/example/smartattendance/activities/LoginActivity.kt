package com.example.smartattendance.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.smartattendance.databinding.ActivityLoginBinding
import com.example.smartattendance.utils.RoleNavigator
import com.example.smartattendance.utils.SessionManager
import com.example.smartattendance.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            if (validateInputs(email, password)) {
                login(email, password)
            }
        }

        binding.registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        return when {
            email.isEmpty() -> {
                binding.emailInput.error = "Email is required"
                false
            }
            password.isEmpty() -> {
                binding.passwordInput.error = "Password is required"
                false
            }
            else -> true
        }
    }

    private fun login(email: String, password: String) {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            authViewModel.login(email, password)
        }
    }

    private fun observeViewModel() {
        authViewModel.loginSuccess.observe(this) { loginResponse ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
            startActivity(
                RoleNavigator.dashboardIntent(this, loginResponse.role).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
            finish()
        }

        authViewModel.error.observe(this) { error ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        }
    }
}
