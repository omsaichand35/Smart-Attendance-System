package com.example.smartattendance.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartattendance.api.RetrofitClient
import com.example.smartattendance.databinding.ActivityCreateCourseBinding
import com.example.smartattendance.models.CreateCourseRequest
import com.example.smartattendance.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateCourseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateCourseBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateCourseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        binding.createButton.setOnClickListener {
            val courseName = binding.courseNameInput.text.toString().trim()
            if (courseName.isNotEmpty()) {
                createCourse(courseName)
            } else {
                Toast.makeText(this, "Please enter course name", Toast.LENGTH_SHORT).show()
            }
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun createCourse(courseName: String) {
        binding.progressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = CreateCourseRequest(courseName)
                val response = RetrofitClient.getApiService(this@CreateCourseActivity).createCourse(request)

                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    if (response.success && response.data != null) {
                        val inviteCode = response.data.inviteCode
                        Toast.makeText(
                            this@CreateCourseActivity,
                            "Course created. Code: $inviteCode",
                            Toast.LENGTH_LONG
                        ).show()
                        startActivity(Intent(this@CreateCourseActivity, DashboardActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@CreateCourseActivity, response.message, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@CreateCourseActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}