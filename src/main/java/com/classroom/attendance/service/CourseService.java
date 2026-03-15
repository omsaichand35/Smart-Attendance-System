package com.classroom.attendance.service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.classroom.attendance.dto.CreateCourseRequest;
import com.classroom.attendance.dto.JoinCourseRequest;
import com.classroom.attendance.entity.Course;
import com.classroom.attendance.entity.Enrollment;
import com.classroom.attendance.entity.User;
import com.classroom.attendance.exception.BadRequestException;
import com.classroom.attendance.exception.UnauthorizedException;
import com.classroom.attendance.repository.CourseRepository;
import com.classroom.attendance.repository.EnrollmentRepository;
import com.classroom.attendance.repository.SessionRepository;
import com.classroom.attendance.repository.UserRepository;
import com.classroom.attendance.security.AuthenticatedUser;
import com.classroom.attendance.security.SecurityUtils;

@Service
public class CourseService {
    public final CourseRepository courseRepository;
    public final EnrollmentRepository enrollmentRepository;
    public final UserRepository userRepository;
    public final SessionRepository sessionRepository;

    public CourseService(CourseRepository courseRepository, EnrollmentRepository enrollmentRepository, UserRepository userRepository, SessionRepository sessionRepository) {
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    public Course createCourse(CreateCourseRequest request) {
        AuthenticatedUser currentUser = SecurityUtils.getCurrentUser();
        if (!"TEACHER".equalsIgnoreCase(currentUser.role())) {
            throw new UnauthorizedException("Only teachers can create courses");
        }

        String inviteCode = generateInviteCode();

        Course course = new Course(
            request.getCourseName(),
            inviteCode,
            currentUser.userId()
        );

        return enrichCourse(courseRepository.save(course));
    }

    private String generateInviteCode() {

        String chars = "ABCDEFGHIJKLMNOPQRUSTVWXYZ1234567890!@#$%^&*()_+";
        Random random = new Random();
        
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }

        return code.toString();
    }

    public List<Course> listCourses() {
        AuthenticatedUser currentUser = SecurityUtils.getCurrentUser();
        if ("TEACHER".equalsIgnoreCase(currentUser.role())) {
            return courseRepository.findByTeacherId(currentUser.userId())
                    .stream()
                    .map(this::enrichCourse)
                    .collect(Collectors.toList());
        }

        return enrollmentRepository.findByStudentId(currentUser.userId())
                .stream()
                .map(Enrollment::getCourse)
                .map(this::enrichCourse)
                .collect(Collectors.toList());
    }

    public Enrollment joinCourse(JoinCourseRequest request){
        AuthenticatedUser currentUser = SecurityUtils.getCurrentUser();
        if (!"STUDENT".equalsIgnoreCase(currentUser.role())) {
            throw new UnauthorizedException("Only students can join courses");
        }

        Course course = courseRepository.findByInviteCode(request.getInviteCode())
                .orElseThrow(() -> new RuntimeException("Invalid invite code"));

        User student = userRepository.findById(currentUser.userId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), course.getId())) {
            throw new BadRequestException("Student already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment(student, course);

        return enrollmentRepository.save(enrollment);
    }

    public Course enrichCourse(Course course) {
        userRepository.findById(course.getTeacherId())
                .ifPresent(teacher -> course.setTeacherName(teacher.getName()));
        course.setStudentCount(enrollmentRepository.countByCourseId(course.getId()));
        course.setSessionCount(sessionRepository.countByCourseId(course.getId()));
        if (course.getAttendancePercent() == null) {
            course.setAttendancePercent(0);
        }
        return course;
    }
}
