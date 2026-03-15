package com.example.smartattendance.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartattendance.api.RetrofitClient
import com.example.smartattendance.databinding.ActivityJoinCourseBinding
import com.example.smartattendance.models.JoinCourseRequest
import com.example.smartattendance.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class JoinCourseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJoinCourseBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinCourseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        binding.joinButton.setOnClickListener {
            val inviteCode = binding.inviteCodeInput.text.toString().trim()
            if (inviteCode.isNotEmpty()) {
                joinCourse(inviteCode)
            } else {
                Toast.makeText(this, "Please enter invite code", Toast.LENGTH_SHORT).show()
            }
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun joinCourse(inviteCode: String) {
        binding.progressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = JoinCourseRequest(inviteCode)
                val response = RetrofitClient.getApiService(this@JoinCourseActivity).joinCourse(request)

                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    if (response.success) {
                        Toast.makeText(this@JoinCourseActivity, "Joined course successfully!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@JoinCourseActivity, DashboardActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@JoinCourseActivity, response.message, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@JoinCourseActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
