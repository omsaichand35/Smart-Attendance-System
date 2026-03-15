package com.classroom.attendance.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.classroom.attendance.dto.ApiResponse;
import com.classroom.attendance.dto.CreateSessionRequest;
import com.classroom.attendance.entity.Session;
import com.classroom.attendance.service.SessionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;



@RestController
@RequestMapping("/session")
public class SessionController {
    private final SessionService sessionService;
    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<Session>> startSession(@RequestBody CreateSessionRequest request) {
        Session session = sessionService.startSession(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Session location set successfully", session));
    }

    @GetMapping("/list/{courseId}")
    public ResponseEntity<ApiResponse<List<Session>>> listSessions(@PathVariable Long courseId) {
        List<Session> sessions = sessionService.listSessions(courseId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Sessions fetched successfully", sessions));
    }
    
    @GetMapping("/test")
    public String test() {
        return "return success";
    }
    

}
