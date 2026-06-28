package com.nitin.CodeForge.auth.service;

import com.nitin.CodeForge.auth.dto.AuthResponse;
import com.nitin.CodeForge.auth.dto.LoginRequest;
import com.nitin.CodeForge.auth.dto.RegisterRequest;
import com.nitin.CodeForge.auth.entity.Role;
import com.nitin.CodeForge.auth.entity.User;
import com.nitin.CodeForge.auth.mapper.UserMapper;
import com.nitin.CodeForge.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request){
        //checking if user exists
        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already exists");
        }

        if(userRepository.existsByUsername(request.getUsername())){
            throw new RuntimeException("Username already exists");
        }

        //converting dto to user using mapper
        User user = userMapper.toEntity(request);

        //setting role
        user.setRole(Role.USER);

        //timestamps
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // bycrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        //save it now
        user = userRepository.save(user);//reassiging coz it will generate id


        // return response
        return  AuthResponse.builder()
                .token("user register successfully")
                .type("success")
                .build();


    }

    public AuthResponse login(LoginRequest request){
        //checking if email exists , dont tell if does not exists for attackers just throw execption
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid Email or Password"));

        //vefiy password , use matches coz evertime new password is generated through hashing so
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid Email or Password");
        }
        // return auth response jwt token
        return AuthResponse.builder()
                .token("Login Successful")
                .type("SUCCESS")
                .build();
    }

}
