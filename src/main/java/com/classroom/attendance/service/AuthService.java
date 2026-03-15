package com.classroom.attendance.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.classroom.attendance.dto.LoginRequest;
import com.classroom.attendance.dto.LoginResponse;
import com.classroom.attendance.dto.RegisterRequest;
import com.classroom.attendance.entity.User;
import com.classroom.attendance.exception.BadRequestException;
import com.classroom.attendance.exception.UnauthorizedException;
import com.classroom.attendance.repository.UserRepository;
import com.classroom.attendance.security.JwtTokenProvider;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public User register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already registered");
        }

        String normalizedRole = request.getRole().toUpperCase();
        String rollNumber = request.getRollNumber();

        if ("STUDENT".equals(normalizedRole)) {
            if (rollNumber == null || rollNumber.isBlank()) {
                throw new BadRequestException("Roll number is required for students");
            }
            if (userRepository.findByRollNumber(rollNumber.trim()).isPresent()) {
                throw new BadRequestException("Roll number already registered");
            }
            rollNumber = rollNumber.trim();
        } else {
            rollNumber = null;
        }

        User user = new User(
            null, request.getName(),
            request.getEmail(),
            passwordEncoder.encode(request.getPassword()),
            normalizedRole,
            rollNumber,
            null
        );

        return userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        // Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        // Generate JWT token
        String token = tokenProvider.generateToken(user.getName(), user.getId(), user.getRole());

        return new LoginResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole(),
            token,
            86400000L // 24 hours in milliseconds
        );
    }
}
