package com.nitin.CodeForge.auth.dto;

import com.nitin.CodeForge.auth.entity.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserResponse {
    private Long id;

    private String username;

    private String email;

    private Role role;
}
