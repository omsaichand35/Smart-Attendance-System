package com.example.smartattendance.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.smartattendance.api.RetrofitClient
import com.example.smartattendance.models.LoginRequest
import com.example.smartattendance.models.LoginResponse
import com.example.smartattendance.utils.SessionManager
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = RetrofitClient.getApiService(application)
    private val sessionManager = SessionManager(application)

    private val _loginSuccess = MutableLiveData<LoginResponse>()
    val loginSuccess: LiveData<LoginResponse> = _loginSuccess

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val request = LoginRequest(email, password)
                val response = apiService.login(request)

                if (response.success && response.data != null) {
                    sessionManager.saveLoginData(response.data)
                    _loginSuccess.value = response.data
                } else {
                    _error.value = response.message
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun register(name: String, email: String, password: String, role: String, rollNumber: String?) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val request = com.example.smartattendance.models.RegisterRequest(
                    name = name,
                    email = email,
                    password = password,
                    role = role,
                    rollNumber = rollNumber?.takeIf { it.isNotBlank() }
                )
                val response = apiService.register(request)

                if (response.success) {
                    _error.value = "Registration successful! Please login."
                } else {
                    _error.value = response.message
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                _isLoading.value = false
            }
        }
    }
}
