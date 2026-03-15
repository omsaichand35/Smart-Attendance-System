package com.classroom.attendance.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "sessions")
public class Session {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private double latitude;
    private double longitude;
    private double radius;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Session() {}

    public Session(Course course, double latitude, double longitude, double radius, LocalDateTime startTime, LocalDateTime endTime) {
        this.course = course;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getId() { return id; }

    public Course getCourse() { return course; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public double getRadius() { return radius; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }

    public void setCourse(Course course) { this.course = course;}
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setRadius(double radius) { this.radius = radius; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
}
