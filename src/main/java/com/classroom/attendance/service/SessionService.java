package com.classroom.attendance.service;

import org.springframework.stereotype.Service;

import com.classroom.attendance.exception.UnauthorizedException;
import com.classroom.attendance.entity.Course;
import com.classroom.attendance.entity.Enrollment;
import com.classroom.attendance.entity.Session;
import com.classroom.attendance.repository.CourseRepository;
import com.classroom.attendance.repository.EnrollmentRepository;
import com.classroom.attendance.repository.SessionRepository;
import com.classroom.attendance.dto.CreateSessionRequest;
import com.classroom.attendance.security.AuthenticatedUser;
import com.classroom.attendance.security.SecurityUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public SessionService(SessionRepository sessionRepository,
                          CourseRepository courseRepository,
                          EnrollmentRepository enrollmentRepository){
        this.sessionRepository = sessionRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public Session startSession(CreateSessionRequest request){
        AuthenticatedUser currentUser = SecurityUtils.getCurrentUser();
        if (!"TEACHER".equalsIgnoreCase(currentUser.role())) {
            throw new UnauthorizedException("Only teachers can set session location");
        }

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getTeacherId().equals(currentUser.userId())) {
            throw new UnauthorizedException("Only the course teacher can set location");
        }

        LocalDateTime start = LocalDateTime.parse(request.getStartTime());
        LocalDateTime end = LocalDateTime.parse(request.getEndTime());

        Session session = new Session(
                course,
                request.getLatitude(),
                request.getLongitude(),
                request.getRadius(),
                start,
                end
        );

        return sessionRepository.save(session);
    }

    public List<Session> listSessions(Long courseId) {
        AuthenticatedUser currentUser = SecurityUtils.getCurrentUser();
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        boolean isTeacher = course.getTeacherId().equals(currentUser.userId());
        boolean isEnrolledStudent = enrollmentRepository.findByStudentIdAndCourseId(currentUser.userId(), courseId)
                .map(Enrollment::getId)
                .isPresent();

        if (!isTeacher && !isEnrolledStudent) {
            throw new UnauthorizedException("You do not have access to this course sessions");
        }

        return sessionRepository.findByCourseIdOrderByStartTimeDesc(courseId);
    }
}