# Smart Attendance App - Complete Implementation

A comprehensive attendance management system combining Android mobile app with Spring Boot backend, featuring Google Classroom-style UI and advanced geofencing capabilities.

## Project Overview

This project consists of two main components:

### 1. Android Mobile Application (Kotlin)
Location: `c:\Users\omsai\AndroidStudioProjects\SmartAttendance`

**Features:**
- Modern Material Design UI inspired by Google Classroom
- User authentication with JWT tokens
- Course enrollment and management
- Real-time location-based attendance marking
- Geofencing validation (100-meter radius)
- Session persistence with SharedPreferences
- Responsive layouts for all screen sizes

**Technology Stack:**
- Kotlin with Coroutines
- Retrofit 2 for REST API calls
- Google Play Services (Location, Maps)
- ViewModel & LiveData for state management
- Material Design Components
- DataStore for preferences

### 2. Backend Service (Spring Boot)
Location: `c:\Users\omsai\6th-sem Projects\smart_attendence\attendance`

**Features:**
- RESTful API endpoints with proper HTTP status codes
- JWT authentication and authorization
- Password encryption with BCrypt
- Comprehensive input validation
- Global exception handling
- Geofencing validation using Haversine formula
- SQL Server database integration
- CORS support

**Technology Stack:**
- Spring Boot 4.0.3
- Spring Security
- JPA/Hibernate ORM
- JJWT for JWT token management
- SQL Server JDBC
- Maven build system
- Swagger/OpenAPI documentation support

## Architecture & Design

### Backend Architecture
```
Authentication Flow:
1. User registers with email, password, name, role
2. Password is hashed using BCrypt
3. User logs in with JWT token generation
4. All subsequent requests include Authorization header with Bearer token
5. JwtAuthenticationFilter validates token on each request
```

### Android Architecture
```
Activity Flow:
MainActivity (Splash) → LoginActivity → DashboardActivity
                    ↓                         ↓
                RegisterActivity        CourseDetailActivity
                                             ↓
                                    MarkAttendanceActivity
                                             ↓
                                        JoinCourseActivity
```

### Data Flow
```
Frontend (Android) --[HTTPS/REST]--> Backend (Spring Boot) --[JDBC]--> SQL Server Database
   └─ API calls with JWT tokens
   └─ Location data in MarkAttendanceRequest
```

## Setup Instructions

### Prerequisites
- Java 11 or higher (for backend)
- Android Studio with Kotlin support
- Microsoft SQL Server (local or remote)
- Gradle 7.0+ (for Android)
- Maven 3.6+ (for backend)
- Android SDK 26+ (minSdk)

### Backend Setup

1. **Database Configuration**
   - Create SQL Server database named `SmartAttendance`
   - Update `application.properties`:
     ```properties
     spring.datasource.url=jdbc:sqlserver://your-server:1433;databaseName=SmartAttendance;integratedSecurity=true;encrypt=false
     ```

2. **Build and Run**
   ```bash
   cd "c:\Users\omsai\6th-sem Projects\smart_attendence\attendance"
   mvn clean install
   mvn spring-boot:run
   ```
   Server runs on `http://localhost:8080`

3. **API Endpoints**
   - **Auth**: POST `/auth/register`, POST `/auth/login`
   - **Courses**: POST `/course/create`, POST `/course/join`, GET `/course/list`
   - **Sessions**: POST `/session/start`, GET `/session/list/{courseId}`
   - **Attendance**: POST `/attendance/mark`, GET `/attendance/session/{sessionId}`

### Android App Setup

1. **Project Configuration**
   - Open project in Android Studio
   - Update `gradle.properties` with JDK version if needed
   - Sync gradle dependencies

2. **API Configuration**
   - Backend URL in `RetrofitClient.kt`:
     ```kotlin
     private const val BASE_URL = "http://10.0.2.2:8080/"  // For emulator
     // or "http://your-device-ip:8080/" for physical device
     ```

3. **Permissions**
   - Location permissions are requested at runtime
   - Grant when prompted during first attendance marking

4. **Build and Run**
   ```bash
   cd SmartAttendance
   ./gradlew build
   ./gradlew installDebug
   ```

## Key Features Implementation

### 1. Authentication System
- **Registration**: Users register with name, email, password, and role (STUDENT/TEACHER)
- **Login**: JWT token generated on successful authentication
- **Token Storage**: Stored in SharedPreferences via SessionManager
- **Auto-Logout**: Token expiration configured for 24 hours

### 2. Course Management
- **Create Course**: Teachers can create courses
- **Join Course**: Students enter 6-character invite codes
- **List Courses**: Dashboard shows enrolled and teaching courses
- **Course Details**: View attendance stats and active sessions

### 3. Attendance Marking
- **Geofencing**: Real-time location validation within 100-meter radius
- **Time Window**: Sessions have start/end times for validation
- **Duplicate Prevention**: Each student marked once per session
- **Distance Display**: Shows current distance from session location

### 4. Google Classroom Style UI
- **Modern Cards**: Course display in Material CardView
- **Color Scheme**: Blue (#3B82F6) brand color with clean typography
- **Responsive Layout**: Adaptive for different screen sizes
- **Intuitive Navigation**: Bottom nav and FAB for quick actions

## API Request/Response Examples

### User Registration
```json
POST /auth/register
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "role": "STUDENT"
}

Response:
{
  "success": true,
  "message": "Registration successful",
  "data": {
    "id": 1,
    "userName": "John Doe",
    "email": "john@example.com",
    "role": "STUDENT"
  }
}
```

### User Login
```json
POST /auth/login
{
  "email": "john@example.com",
  "password": "password123"
}

Response:
{
  "success": true,
  "message": "Login successful",
  "data": {
    "userId": 1,
    "username": "John Doe",
    "email": "john@example.com",
    "role": "STUDENT",
    "token": "eyJhbGc....",
    "expiresIn": 86400000
  }
}
```

### Mark Attendance
```json
POST /attendance/mark
{
  "studentId": 1,
  "sessionId": 1,
  "latitude": 28.7041,
  "longitude": 77.1025
}

Response:
{
  "success": true,
  "message": "Attendance marked successfully",
  "data": {
    "id": 1,
    "studentId": 1,
    "sessionId": 1,
    "timestamp": "2024-03-12T14:30:00"
  }
}
```

## Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY IDENTITY,
    user_name NVARCHAR(255),
    email NVARCHAR(255) UNIQUE,
    password NVARCHAR(255),
    role NVARCHAR(50),
    created_at BIGINT
)
```

### Courses Table
```sql
CREATE TABLE courses (
    id BIGINT PRIMARY KEY IDENTITY,
    course_name NVARCHAR(255),
    invite_code NVARCHAR(10) UNIQUE,
    teacher_id BIGINT FOREIGN KEY REFERENCES users(id)
)
```

### Enrollments Table
```sql
CREATE TABLE enrollments (
    id BIGINT PRIMARY KEY IDENTITY,
    student_id BIGINT FOREIGN KEY REFERENCES users(id),
    course_id BIGINT FOREIGN KEY REFERENCES courses(id)
)
```

### Sessions Table
```sql
CREATE TABLE sessions (
    id BIGINT PRIMARY KEY IDENTITY,
    course_id BIGINT FOREIGN KEY REFERENCES courses(id),
    latitude FLOAT,
    longitude FLOAT,
    radius FLOAT,
    start_time DATETIME,
    end_time DATETIME
)
```

### Attendance Table
```sql
CREATE TABLE attendance (
    id BIGINT PRIMARY KEY IDENTITY,
    student_id BIGINT FOREIGN KEY REFERENCES users(id),
    session_id BIGINT FOREIGN KEY REFERENCES sessions(id),
    timestamp DATETIME
)
```

## Security Features

1. **Password Security**
   - Passwords hashed with BCrypt
   - Never stored in plaintext
   - Minimum 6 characters enforced

2. **JWT Authentication**
   - Signed with HS512 algorithm
   - 24-hour expiration
   - Includes userId and role in payload

3. **CORS Configuration**
   - Restricted to allowed origins
   - Credentials support enabled
   - Preflight requests handled

4. **Input Validation**
   - All DTOs validated with annotations
   - Email format validation
   - Required field checks
   - Custom error messages

## Testing

### Backend Testing
```bash
# Test registration
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Test User","email":"test@example.com","password":"test123","role":"STUDENT"}'

# Test login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"test123"}'
```

### Android Testing
- Use Android Emulator with Google APIs for location services
- Test with mock location in Developer Settings
- Verify SharedPreferences storage in App Inspector
- Check network calls in Android Studio Profiler

## Troubleshooting

### Common Issues

1. **Database Connection Error**
   - Verify SQL Server is running
   - Check JDBC connection string matches your setup
   - Ensure database exists or allow auto-creation with `ddl-auto=update`

2. **API Call Failures**
   - Check backend is running on port 8080
   - Verify correct IP/port in RetrofitClient
   - Check network connectivity
   - Ensure CORS is properly configured

3. **Location Permission Issues**
   - Request permission at runtime for Android 6.0+
   - Check manifest has location permissions declared
   - Verify GPS is enabled on device/emulator

4. **JWT Token Expired**
   - User needs to login again
   - Token automatically refreshed on login
   - Check system time is correct

## Performance Optimizations

1. **Network**
   - HTTP caching enabled in OkHttpClient
   - Connection pooling reduces overhead
   - Gzip compression for API responses

2. **Database**
   - Indexed email field for faster lookups
   - Connection pooling with HikariCP
   - Query optimization with JPA

3. **Android**
   - ViewModel caches reduce network calls
   - Location updates use FusedLocationProvider
   - Image loading optimized with Glide

## Future Enhancements

1. **Real-time Updates**: Add WebSocket support for live attendance notifications
2. **QR Code Generation**: Generate QR codes for quick attendance marking
3. **Video Integration**: Support for class recordings and streaming
4. **Analytics Dashboard**: Detailed attendance reports and analytics
5. **Offline Support**: Local database sync with backend
6. **Multi-language Support**: Localization for different languages
7. **Push Notifications**: Remind students of active sessions
8. **Advanced Geofencing**: WiFi-based location tracking
9. **Biometric Authentication**: Fingerprint/Face recognition
10. **Role-based Features**: Teacher dashboard with class management

## Contributing

This project is part of a 6th semester assignment for a Smart Attendance System. For modifications or improvements, follow the existing code structure and patterns.

## License

This project is created for educational purposes. Modify and use as needed for your requirements.

## Support & Documentation

- Backend API Documentation: `http://localhost:8080/swagger-ui.html`
- Android Architecture Components: AndroidX Documentation
- Google Play Services: https://developers.google.com/android/guides
- Spring Boot: https://spring.io/projects/spring-boot

---

**Project Completion Date**: March 12, 2026
**Total Implementation Time**: Comprehensive rewrite with modern architecture and best practices
