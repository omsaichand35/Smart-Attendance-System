package com.example.smartattendance.models

data class MarkAttendanceRequest(
    val sessionId: Long,
    val latitude: Double,
    val longitude: Double
)
