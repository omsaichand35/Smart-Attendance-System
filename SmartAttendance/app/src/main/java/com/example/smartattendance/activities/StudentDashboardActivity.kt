package com.example.smartattendance.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.smartattendance.adapters.CourseAdapter
import com.example.smartattendance.databinding.ActivityDashboardBinding
import com.example.smartattendance.utils.RoleNavigator
import com.example.smartattendance.utils.SessionManager
import com.example.smartattendance.viewmodels.DashboardViewModel

class StudentDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private lateinit var sessionManager: SessionManager
    private lateinit var courseAdapter: CourseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        if (sessionManager.getRole().equals("TEACHER", ignoreCase = true)) {
            startActivity(RoleNavigator.dashboardIntent(this, sessionManager.getRole()))
            finish()
            return
        }

        setupRecyclerView()
        configureRoleUi()
        setupClickListeners()
        observeViewModel()
        dashboardViewModel.loadCourses()
    }

    private fun configureRoleUi() {
        binding.fabJoinCourse.contentDescription = "Join Course"
        binding.tabTeaching.visibility = android.view.View.GONE
        binding.tabEnrolled.text = "My Courses"
    }

    private fun setupRecyclerView() {
        binding.coursesRecyclerView.apply {
            layoutManager = GridLayoutManager(this@StudentDashboardActivity, 2)
            courseAdapter = CourseAdapter(emptyList()) { course ->
                val intent = Intent(this@StudentDashboardActivity, CourseDetailActivity::class.java)
                intent.putExtra("courseId", course.id)
                intent.putExtra("courseName", course.courseName)
                intent.putExtra("teacherName", course.teacherName ?: "Professor")
                intent.putExtra("studentCount", course.studentCount ?: 0)
                intent.putExtra("sessionCount", course.sessionCount ?: 0)
                intent.putExtra("attendancePercent", course.attendancePercent ?: 0)
                startActivity(intent)
            }
            adapter = courseAdapter
        }
    }

    private fun setupClickListeners() {
        binding.logoutButton.setOnClickListener {
            sessionManager.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.fabJoinCourse.setOnClickListener {
            startActivity(Intent(this, JoinCourseActivity::class.java))
        }
    }

    private fun observeViewModel() {
        dashboardViewModel.courses.observe(this) { courses ->
            courseAdapter = CourseAdapter(courses) { course ->
                val intent = Intent(this, CourseDetailActivity::class.java)
                intent.putExtra("courseId", course.id)
                intent.putExtra("courseName", course.courseName)
                intent.putExtra("teacherName", course.teacherName ?: "Professor")
                intent.putExtra("studentCount", course.studentCount ?: 0)
                intent.putExtra("sessionCount", course.sessionCount ?: 0)
                intent.putExtra("attendancePercent", course.attendancePercent ?: 0)
                startActivity(intent)
            }
            binding.coursesRecyclerView.adapter = courseAdapter
        }

        dashboardViewModel.error.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        }
    }
}
