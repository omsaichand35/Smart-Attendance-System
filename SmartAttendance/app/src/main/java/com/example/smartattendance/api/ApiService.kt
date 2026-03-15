package com.example.smartattendance.api

import com.example.smartattendance.models.*
import retrofit2.http.*

interface ApiService {

    // Auth endpoints
    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): ApiResponse<User>

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): ApiResponse<LoginResponse>

    // Course endpoints
    @POST("course/join")
    suspend fun joinCourse(
        @Body request: JoinCourseRequest
    ): ApiResponse<Enrollment>

    @POST("course/create")
    suspend fun createCourse(
        @Body request: CreateCourseRequest
    ): ApiResponse<Course>

    @GET("course/list")
    suspend fun listCourses(): ApiResponse<List<Course>>

    // Session endpoints
    @POST("session/start")
    suspend fun startSession(
        @Body request: CreateSessionRequest
    ): ApiResponse<Session>

    @GET("session/list/{courseId}")
    suspend fun listSessions(
        @Path("courseId") courseId: Long
    ): ApiResponse<List<Session>>

    // Attendance endpoints
    @POST("attendance/mark")
    suspend fun markAttendance(
        @Body request: MarkAttendanceRequest
    ): ApiResponse<Attendance>

    @GET("attendance/session/{sessionId}")
    suspend fun getSessionAttendance(
        @Path("sessionId") sessionId: Long
    ): ApiResponse<List<Attendance>>
}
