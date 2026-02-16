package com.saptarshi.DemoInterview.controller;

import com.saptarshi.DemoInterview.dto.AuthResponse;
import com.saptarshi.DemoInterview.dto.SignUpRequest;
import com.saptarshi.DemoInterview.dto.UserLogInRequest;
import com.saptarshi.DemoInterview.entity.AppUser;
import com.saptarshi.DemoInterview.exception.ConflictException;
import com.saptarshi.DemoInterview.exception.ResourceNotFoundException;
import com.saptarshi.DemoInterview.jwt.JwtService;
import com.saptarshi.DemoInterview.repository.AppUserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final AppUserRepository appUserRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/sign-in")
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpRequest request) {
        var existingUser = appUserRepository.findByEmail(request.getEmail());
        if (existingUser != null) {
            throw new ConflictException("User already exists");
        }

        var user = new AppUser();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        appUserRepository.save(user);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/log-in")
    public ResponseEntity<AuthResponse> logIn(@Valid @RequestBody UserLogInRequest request) {
        var user = appUserRepository.findByEmail(request.getEmail());
        if (user == null) {
            throw new ResourceNotFoundException("User does not exist");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        return ResponseEntity.ok(AuthResponse.builder()
                .token(jwtService.generateToken(user))
                .tokenType("Bearer")
                .expiresInMinutes(jwtService.getTokenExpirationMinutes())
                .build());
    }
}
