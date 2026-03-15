package com.example.smartattendance.utils

import android.content.Context
import com.example.smartattendance.models.LoginResponse

class SessionManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences(
        "smart_attendance_prefs",
        Context.MODE_PRIVATE
    )

    companion object {
        const val USER_ID = "user_id"
        const val EMAIL = "email"
        const val USERNAME = "username"
        const val ROLE = "role"
        const val TOKEN = "auth_token"
        const val IS_LOGGED_IN = "is_logged_in"
    }

    fun saveLoginData(loginResponse: LoginResponse) {
        sharedPreferences.edit().apply {
            putLong(USER_ID, loginResponse.userId)
            putString(EMAIL, loginResponse.email)
            putString(USERNAME, loginResponse.username)
            putString(ROLE, loginResponse.role)
            putString(TOKEN, loginResponse.token)
            putBoolean(IS_LOGGED_IN, true)
            apply()
        }
    }

    fun getToken(): String? {
        return sharedPreferences.getString(TOKEN, null)
    }

    fun getUserId(): Long {
        return sharedPreferences.getLong(USER_ID, -1)
    }

    fun getUsername(): String? {
        return sharedPreferences.getString(USERNAME, null)
    }

    fun getEmail(): String? {
        return sharedPreferences.getString(EMAIL, null)
    }

    fun getRole(): String? {
        return sharedPreferences.getString(ROLE, null)
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false)
    }

    fun logout() {
        sharedPreferences.edit().clear().apply()
    }
}
