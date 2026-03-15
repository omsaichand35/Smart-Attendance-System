package com.example.smartattendance.models

data class CreateSessionRequest(
    val courseId: Long,
    val latitude: Double,
    val longitude: Double,
    val radius: Double,
    val startTime: String,
    val endTime: String
)
