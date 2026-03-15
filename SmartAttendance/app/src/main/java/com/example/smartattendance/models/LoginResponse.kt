package com.example.smartattendance.models

data class LoginResponse(
    val userId: Long,
    val username: String,
    val email: String,
    val role: String,
    val token: String,
    val expiresIn: Long
)
