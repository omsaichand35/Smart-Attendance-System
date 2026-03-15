package com.classroom.attendance.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.classroom.attendance.entity.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findBySessionIdAndStudentId(Long sessionId, Long studentId);   
    List<Attendance> findBySessionId(Long sessionId);
}
