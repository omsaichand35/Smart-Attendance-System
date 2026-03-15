package com.classroom.attendance.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.classroom.attendance.dto.ApiResponse;
import com.classroom.attendance.dto.MarkAttendanceRequest;
import com.classroom.attendance.entity.Attendance;
import com.classroom.attendance.service.AttendanceService;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService){
        this.attendanceService = attendanceService;
    }

    @PostMapping("/mark")
    public ResponseEntity<ApiResponse<Attendance>> markAttendance(@RequestBody MarkAttendanceRequest request){
        Attendance attendance = attendanceService.markAttendance(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Attendance marked successfully", attendance));
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<ApiResponse<List<Attendance>>> getSessionAttendance(@PathVariable Long sessionId){
        List<Attendance> attendanceList = attendanceService.getSessionAttendance(sessionId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Attendance fetched successfully", attendanceList));
    }
}