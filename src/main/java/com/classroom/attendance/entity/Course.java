package com.classroom.attendance.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "courses")
public class Course {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String courseName;

    @Column(unique = true)
    private String inviteCode;

    @JsonIgnore
    private Long teacherId;

    @Transient
    private String teacherName;

    @Transient
    private Integer studentCount;

    @Transient
    private Integer sessionCount;

    @Transient
    private Integer attendancePercent;

    public Course() {}

    public Course(String courseName, String inviteCode, Long teacherId) {
        this.courseName = courseName;
        this.inviteCode = inviteCode;
        this.teacherId = teacherId;
    }

    public Long getId() { return id; }

    public String getCourseName() { return courseName; }
    public Long getTeacherId() { return teacherId; }
    public String getInviteCode() { return inviteCode; }
    public String getTeacherName() { return teacherName; }
    public Integer getStudentCount() { return studentCount; }
    public Integer getSessionCount() { return sessionCount; }
    public Integer getAttendancePercent() { return attendancePercent; }

    public void setCourseName(String courseName) { this.courseName = courseName;}
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
    public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    public void setStudentCount(Integer studentCount) { this.studentCount = studentCount; }
    public void setSessionCount(Integer sessionCount) { this.sessionCount = sessionCount; }
    public void setAttendancePercent(Integer attendancePercent) { this.attendancePercent = attendancePercent; }

}
