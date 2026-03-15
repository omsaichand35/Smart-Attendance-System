package com.classroom.attendance.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "attendance")
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private Session session;

    private LocalDateTime timestamp;
    public Attendance() {}

    public Attendance(User student, Session session, LocalDateTime timestamp) {
        this.student = student;
        this.session = session;
        this.timestamp = timestamp;
    }

    public Long getId(){ return id; }
    public User getStudent(){ return student; }
    public Session getSession(){ return session; }
    public LocalDateTime getTimestamp(){ return timestamp; }
    
    public void setStudent(User student){ this.student = student; }
    public void setSession(Session session){ this.session = session; }
    public void setTimestamp(LocalDateTime timestamp){ this.timestamp = timestamp; }
}
