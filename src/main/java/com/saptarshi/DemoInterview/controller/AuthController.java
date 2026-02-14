package com.saptarshi.DemoInterview.controller;

import com.saptarshi.DemoInterview.dto.UserLogInRequest;
import com.saptarshi.DemoInterview.entity.AppUser;
import com.saptarshi.DemoInterview.jwt.JwtService;
import com.saptarshi.DemoInterview.repository.AppUserRepository;
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
    public ResponseEntity<AppUser> signUp(@RequestBody AppUser appUser) {
        var user = appUserRepository.findByEmail(appUser.getEmail());
        if(user != null) {
            throw new RuntimeException("User already Exist");
        }
        user = new AppUser();
        user.setId(appUser.getId());
        user.setPassword(passwordEncoder.encode(appUser.getPassword()));
        user.setEmail(appUser.getEmail());
        user.setRole(appUser.getRole());

        return ResponseEntity.ok(appUserRepository.save(user));

    }

    @PostMapping("/log-in")
    public ResponseEntity<String> logIn(@RequestBody UserLogInRequest request){
        var user = appUserRepository.findByEmail(request.getEmail());
        if(user==null){
            return ResponseEntity.status(401).build();
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        return ResponseEntity.ok(jwtService.generateToken(user));
    }
}
