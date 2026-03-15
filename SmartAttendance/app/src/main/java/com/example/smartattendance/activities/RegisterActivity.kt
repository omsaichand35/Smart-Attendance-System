package com.example.smartattendance.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.smartattendance.databinding.ActivityRegisterBinding
import com.example.smartattendance.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRoleSpinner()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupRoleSpinner() {
        val roles = arrayOf("STUDENT", "TEACHER")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)
        binding.roleSpinner.adapter = adapter
        binding.roleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                val isStudent = roles[position] == "STUDENT"
                binding.rollNumberLayout.visibility = if (isStudent) View.VISIBLE else View.GONE
                if (!isStudent) {
                    binding.rollNumberInput.setText("")
                    binding.rollNumberInput.error = null
                }
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
        }
    }

    private fun setupClickListeners() {
        binding.registerButton.setOnClickListener {
            val name = binding.nameInput.text.toString().trim()
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()
            val role = binding.roleSpinner.selectedItem.toString()
            val rollNumber = binding.rollNumberInput.text.toString().trim()

            if (validateInputs(name, email, password, role, rollNumber)) {
                register(name, email, password, role, rollNumber)
            }
        }

        binding.backButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun validateInputs(name: String, email: String, password: String, role: String, rollNumber: String): Boolean {
        return when {
            name.isEmpty() -> {
                binding.nameInput.error = "Name is required"
                false
            }
            email.isEmpty() -> {
                binding.emailInput.error = "Email is required"
                false
            }
            password.length < 6 -> {
                binding.passwordInput.error = "Password must be at least 6 characters"
                false
            }
            role == "STUDENT" && rollNumber.isEmpty() -> {
                binding.rollNumberInput.error = "Roll number is required for students"
                false
            }
            else -> true
        }
    }

    private fun register(name: String, email: String, password: String, role: String, rollNumber: String) {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            authViewModel.register(name, email, password, role, rollNumber)
        }
    }

    private fun observeViewModel() {
        authViewModel.error.observe(this) { message ->
            binding.progressBar.visibility = View.GONE
            if (message.contains("successful")) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                // Redirect to login after successful registration
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }
}