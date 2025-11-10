package com.virtual.assistance.UserManagement.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.virtual.assistance.UserManagement.dtos.AuthRequest;
import com.virtual.assistance.UserManagement.models.UserInfo;
import com.virtual.assistance.UserManagement.repositories.UserRepository;
import com.virtual.assistance.UserManagement.security.JwtUtil;
import com.virtual.assistance.UserManagement.services.CustomUserDetailsService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Invalid credentials!");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        UserInfo user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        String roleName = user.getRole().getRoleName();

        // ✅ Pass the role to the token
        String token = jwtUtil.generateToken(userDetails.getUsername(), roleName);

        return ResponseEntity.ok().body(Map.of(
                "token", token,
                "email", user.getEmail(),
                "role", roleName));
    }
}
