package com.example.smartattendance.models

data class Session(
    val id: Long,
    val course: Course,
    val longitude: Double,
    val latitude: Double,
    val startTime: String,
    val endTime: String,
    val radius: Double
)
