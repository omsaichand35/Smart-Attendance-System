package com.example.smartattendance.models

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String,
    val rollNumber: String? = null
)
