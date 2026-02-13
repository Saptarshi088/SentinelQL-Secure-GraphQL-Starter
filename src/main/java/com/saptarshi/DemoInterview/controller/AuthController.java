package com.saptarshi.DemoInterview.controller;

import com.saptarshi.DemoInterview.entity.AppUser;
import com.saptarshi.DemoInterview.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
}
