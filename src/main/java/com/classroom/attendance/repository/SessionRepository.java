package com.classroom.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.classroom.attendance.entity.Session;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long>{
	List<Session> findByCourseIdOrderByStartTimeDesc(Long courseId);
	int countByCourseId(Long courseId);
}
