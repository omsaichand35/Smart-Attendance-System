package com.example.smartattendance.models

data class Attendance(
    val id: Long,
    val student: User,
    val session: Session,
    val timeStamp: String
)