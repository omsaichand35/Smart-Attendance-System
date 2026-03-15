package com.classroom.attendance.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "enrollments")
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="student_id")
    private User student;

    @ManyToOne
    @JoinColumn(name="course_id")
    private Course course;

    public Enrollment(){}

    public Enrollment(User student, Course course){
        this.student = student;
        this.course = course;
    }

    public Long getId(){ return id; }

    public User getStudent(){ return student; }

    public Course getCourse(){ return course; }

    public void setStudent(User student){ this.student = student; }

    public void setCourse(Course course){ this.course = course; }
}