package com.classroom.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.classroom.attendance.entity.Course;
import java.util.Optional;
import java.util.List;


public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByInviteCode(String inviteCode);
    List<Course> findByTeacherId(Long teacherId);
}
