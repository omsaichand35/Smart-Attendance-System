package com.example.smartattendance.models

data class User(
    val id: Long,
    val name: String,
    val email: String,
    val role: String,
    val rollNumber: String? = null
)
