package com.nitin.CodeForge.auth.mapper;


import com.nitin.CodeForge.auth.dto.RegisterRequest;
import com.nitin.CodeForge.auth.dto.UserResponse;
import com.nitin.CodeForge.auth.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(RegisterRequest request) {
        User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())   // plain password for now
                .build();
        return null;
    }

    public UserResponse toResponse(User user) {
        return null;
    }

}
