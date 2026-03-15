package com.classroom.attendance.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.classroom.attendance.dto.ApiResponse;
import com.classroom.attendance.dto.CreateCourseRequest;
import com.classroom.attendance.dto.JoinCourseRequest;
import com.classroom.attendance.entity.Course;
import com.classroom.attendance.entity.Enrollment;
import com.classroom.attendance.service.CourseService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;



@RestController
@RequestMapping("/course")
public class CourseController {
    private final CourseService courseService;
    
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Course>> createCourse(@RequestBody CreateCourseRequest request) {
        Course course = courseService.createCourse(request);
        ApiResponse<Course> response = new ApiResponse<>(true, "Course created successfully", course);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<Course>>> listCourses() {
        List<Course> courses = courseService.listCourses();
        ApiResponse<List<Course>> response = new ApiResponse<>(true, "Courses fetched successfully", courses);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    public String test() {
        return "Return test successfull";
    }

    @PostMapping("/join")
    public ResponseEntity<ApiResponse<Enrollment>> joinCourse(@RequestBody JoinCourseRequest request){
        Enrollment enrollment = courseService.joinCourse(request);
        ApiResponse<Enrollment> response = new ApiResponse<>(true, "Joined course successfully", enrollment);
        return ResponseEntity.ok(response);
    }
}
