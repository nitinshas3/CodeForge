package com.nitin.CodeForge.auth.service;

import com.nitin.CodeForge.auth.dto.AuthResponse;
import com.nitin.CodeForge.auth.dto.LoginRequest;
import com.nitin.CodeForge.auth.dto.RegisterRequest;
import com.nitin.CodeForge.auth.entity.Role;
import com.nitin.CodeForge.auth.entity.User;
import com.nitin.CodeForge.auth.mapper.UserMapper;
import com.nitin.CodeForge.auth.repository.UserRepository;
import com.nitin.CodeForge.common.config.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {

        //checking if user exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        //converting dto to user using mapper
        User user = userMapper.toEntity(request);

        //setting role
        user.setRole(Role.USER);

        //timestamps
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        //bcrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        //save it now
        user = userRepository.save(user); //reassigning coz it will generate id

        //generate jwt token
        String token = jwtService.generateToken(user);

        // return response
        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .build();

    }

    public AuthResponse login(LoginRequest request) {

        /*
        this is one way without using spring security ,
        if we use spring security all this will be done by
        AuthenticationManager -> UserDetailsService -> PasswordEncoder etc.

        //checking if email exists , dont tell if does not exists for attackers just throw exception
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid Email or Password"));

        //verify password , use matches coz everytime new password is generated through hashing
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid Email or Password");
        }

        //generate jwt token
        String token = jwtService.generateToken(user);

        // return auth response jwt token
        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .build();
        */


        //this is the spring security implementation

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        //authentication successful, now fetch user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        //generate jwt token
        String token = jwtService.generateToken(user);

        //return response
        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .build();

    }

}