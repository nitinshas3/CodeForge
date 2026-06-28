package com.nitin.CodeForge.common.config;

import com.nitin.CodeForge.auth.service.CustomUserDetailService; // must implement UserDetailsService
import com.nitin.CodeForge.common.config.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailService customUserDetailService; // must implement UserDetailsService

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //@Bean
   // public AuthenticationProvider authenticationProvider() {
     //   DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
     //   authProvider.setUserDetailsService(customUserDetailService); // ✅ works if service implements UserDetailsService
      //  authProvider.setPasswordEncoder(passwordEncoder());
      //  return authProvider;
   // }
}
