package com.example.smartattendance.activities

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.example.smartattendance.databinding.ActivityCourseDetailBinding
import com.example.smartattendance.utils.SessionManager

class CourseDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCourseDetailBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)

        val courseId = intent.getLongExtra("courseId", -1)
        val courseName = intent.getStringExtra("courseName") ?: "Course"
        val teacherName = intent.getStringExtra("teacherName") ?: "Not assigned"
        val studentCount = intent.getIntExtra("studentCount", 0)
        val sessionCount = intent.getIntExtra("sessionCount", 0)
        val attendancePercent = intent.getIntExtra("attendancePercent", 0).coerceIn(0, 100)
        val isTeacher = sessionManager.getRole().equals("TEACHER", ignoreCase = true)

        binding.apply {
            courseTitle.text = courseName
            professorNameText.text = "Professor: $teacherName"
            markAttendanceButton.text = if (isTeacher) "Set Location" else "Mark Attendance"
            backButton.setOnClickListener { finish() }
            markAttendanceButton.setOnClickListener {
                val intent = android.content.Intent(this@CourseDetailActivity, MarkAttendanceActivity::class.java)
                intent.putExtra("courseId", courseId)
                intent.putExtra("courseName", courseName)
                intent.putExtra("isTeacher", isTeacher)
                startActivity(intent)
            }

            animateCounter(studentCountText, studentCount)
            animateCounter(sessionCountText, sessionCount)
            animateAttendance(attendancePercent)
        }
    }

    private fun animateCounter(targetView: android.widget.TextView, targetValue: Int) {
        ValueAnimator.ofInt(0, targetValue.coerceAtLeast(0)).apply {
            duration = 700
            interpolator = DecelerateInterpolator()
            addUpdateListener { animation ->
                targetView.text = animation.animatedValue.toString()
            }
            start()
        }
    }

    private fun animateAttendance(targetPercent: Int) {
        ValueAnimator.ofInt(0, targetPercent).apply {
            duration = 900
            interpolator = DecelerateInterpolator()
            addUpdateListener { animation ->
                val value = animation.animatedValue as Int
                binding.attendanceProgress.progress = value
                binding.attendancePercentText.text = "$value%"
            }
            start()
        }
    }
}
