# Smart Attendance - Backend Status ✅

## Backend Server - RUNNING 

**Status:** ✅ **ACTIVE ON PORT 8080**

The Spring Boot backend is now running with:
- ✅ JWT Authentication configured
- ✅ CORS enabled for mobile apps  
- ✅ SQL Server database connected
- ✅ All REST API endpoints available
- ✅ Spring Security initialized

---

## Available Endpoints

### Authentication Endpoints
```
POST /auth/register      - Register new user
POST /auth/login         - Login and get JWT token
GET  /auth/test          - Test endpoint
```

### Course Endpoints
```
POST /course/create      - Create new course (Teacher only)
POST /course/join        - Join course with invite code
GET  /course/list        - Get all courses
```

### Session Endpoints
```
POST /session/start      - Start attendance session
GET  /session/list/{id}  - Get sessions for course
```

### Attendance Endpoints
```
POST /attendance/mark    - Mark attendance with location
GET  /attendance/session/{id} - Get attendance records
```

---

## Test the API

### Using PowerShell (Curl):
```powershell
# Test endpoint
curl.exe http://localhost:8080/auth/test

# Register User
curl.exe -X POST http://localhost:8080/auth/register `
  -H "Content-Type: application/json" `
  -d '{
    "name":"John Doe",
    "email":"john@test.com",
    "password":"Test@123",
    "role":"STUDENT"
  }'

# Login
curl.exe -X POST http://localhost:8080/auth/login `
  -H "Content-Type: application/json" `
  -d '{
    "email":"john@test.com",
    "password":"Test@123"
  }'
```

---

## API Documentation

Access Swagger UI (interactive documentation) at:
```
http://localhost:8080/swagger-ui.html
```

View all endpoints and test them directly in the browser!

---

## Next Steps

### Option 1: Build & Run Android App
```powershell
cd C:\Users\omsai\AndroidStudioProjects\SmartAttendance
./gradlew build
./gradlew installDebug
```

### Option 2: Test Backend with Postman/Thunder Client
- Import the API collection
- Use Bearer token from login response for authenticated endpoints
- Test all CRUD operations

### Option 3: Use curl from PowerShell
```powershell
# Create user
curl.exe -X POST http://localhost:8080/auth/register -H "Content-Type: application/json" -d '...'

# Login and get token
curl.exe -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d '...'

# Use token for protected endpoints
curl.exe http://localhost:8080/course/list -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Database

Connected to: **SQL Server**
Database: **SmartAttendance**
Tables: users, courses, enrollments, sessions, attendance

---

## Logs

If you need to view server logs:
- Spring Boot outputs to console
- Check `target/logs/` directory for detailed logs
- Application properties in `src/main/resources/application.properties`

---

## Stopping the Server

In PowerShell terminal running the server:
```
Press Ctrl+C to stop
```

---

## Quick Test Flow

1. **Register**: Create a test user account
2. **Login**: Get JWT token
3. **Create Course** (as teacher): Get invite code
4. **Join Course** (as student): Use invite code  
5. **Start Session** (as teacher): Set location and time
6. **Mark Attendance** (as student): Submit location

---

**Status: ✅ Ready for testing!**

The Android app can now connect to this backend for all operations.
