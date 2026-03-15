package com.example.smartattendance.models

data class Enrollment(
    val id: Long,
    val student: User,
    val course: Course
)
