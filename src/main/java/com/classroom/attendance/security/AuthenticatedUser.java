package com.classroom.attendance.security;

public record AuthenticatedUser(Long userId, String username, String role) {
}
