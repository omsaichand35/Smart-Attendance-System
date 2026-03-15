package com.example.smartattendance.models

data class Course(
    val id: Long,
    val courseName: String,
    val inviteCode: String,
    val teacherId: Long,
    val teacherName: String? = null,
    val studentCount: Int? = null,
    val sessionCount: Int? = null,
    val attendancePercent: Int? = null
)
