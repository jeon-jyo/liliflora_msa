package com.osio.userservice.global.security;

import com.osio.userservice.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;

    // HttpSecurity 를 구성하여 보안 설정을 정의
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // REST API 이므로 basic auth 및 csrf 보안을 사용하지 않음 (Basic 인증 x)
                .httpBasic(custom-> custom.disable())
                // CSRF(Cross-Site Request Forgery) 보안을 비활성화
                .csrf(custom->custom.disable())
                // JWT를 사용하기 때문에 세션을 사용하지 않음
                .sessionManagement(config->config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 요청에 대한 인가 규칙 설정
                .authorizeHttpRequests(http->{
                    http
                            .requestMatchers("/**").permitAll();    // user + feign
                })
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt Encoder 사용
        // DelegatingPasswordEncoder를 생성하여 반환
        // DelegatingPasswordEncoder는 여러 암호화 알고리즘을 지원하며, Spring Security의 기본 권장 알고리즘을 사용하여 비밀번호를 인코딩
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}