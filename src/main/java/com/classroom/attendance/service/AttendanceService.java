package com.classroom.attendance.service;

import org.springframework.stereotype.Service;

import com.classroom.attendance.entity.*;
import com.classroom.attendance.exception.UnauthorizedException;
import com.classroom.attendance.repository.*;
import com.classroom.attendance.dto.MarkAttendanceRequest;
import com.classroom.attendance.security.AuthenticatedUser;
import com.classroom.attendance.security.SecurityUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    public AttendanceService(
            AttendanceRepository attendanceRepository,
            SessionRepository sessionRepository,
            UserRepository userRepository,
            EnrollmentRepository enrollmentRepository) {

        this.attendanceRepository = attendanceRepository;
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public Attendance markAttendance(MarkAttendanceRequest request){
        AuthenticatedUser currentUser = SecurityUtils.getCurrentUser();
        if (!"STUDENT".equalsIgnoreCase(currentUser.role())) {
            throw new UnauthorizedException("Only students can mark attendance");
        }

        Session session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new RuntimeException("Session not found"));

        User student = userRepository.findById(currentUser.userId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (!enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), session.getCourse().getId())) {
            throw new UnauthorizedException("Student is not enrolled in this course");
        }

        // 1. Check duplicate attendance
        attendanceRepository
                .findBySessionIdAndStudentId(session.getId(), student.getId())
                .ifPresent(a -> {
                    throw new RuntimeException("Attendance already marked");
                });

        // 2. Check session time window
        LocalDateTime now = LocalDateTime.now();

        if(now.isBefore(session.getStartTime()) || now.isAfter(session.getEndTime())){
            throw new RuntimeException("Session not active");
        }

        // 3. Check geofence distance
        double distance = calculateDistance(
                request.getLatitude(),
                request.getLongitude(),
                session.getLatitude(),
                session.getLongitude()
        );

        if(distance > session.getRadius()){
            throw new RuntimeException("You are outside classroom radius");
        }

        Attendance attendance = new Attendance(
                student,
                session,
                LocalDateTime.now()
        );

        return attendanceRepository.save(attendance);
    }

    public List<Attendance> getSessionAttendance(Long sessionId){
        return attendanceRepository.findBySessionId(sessionId);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c * 1000; // Return distance in meters
    }
}