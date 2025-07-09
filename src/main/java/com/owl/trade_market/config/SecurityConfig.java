package com.owl.trade_market.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web
        .builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    // 기존 PasswordEncoder 빈
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 이게 새로 추가되는 보안 설정입니다.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1) URL별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 정적 리소스와 인증 없이 열어 줄 엔드포인트
                        .requestMatchers(
                                "/", "/main",
                                "/css/**", "/js/**", "/images/**",
                                "/register", "/users/register",
                                "/login", "/users/login"
                        ).permitAll()
                        // 그 외에는 모두 인증 필요
                        .anyRequest().authenticated()
                )
                // 2) 커스텀 로그인 페이지 설정
                .formLogin(form -> form
                        .loginPage("/login")      // 내가 만든 /login 컨트롤러/뷰 사용
                        .usernameParameter("userId")    // 커스텀 username사용(기본: username)
                        .passwordParameter("userPassword") // 커스텀 password사용(기본: password)
                        .loginProcessingUrl("/users/login") // form action URL
                        .failureUrl("/login?error") //추가
                        .defaultSuccessUrl("/main", true)   // 로그인 성공 후 리다이렉트
                        .permitAll()
                )
                // 3) 로그아웃 설정 (원하면)
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/main")
                        .permitAll()
                );

        return http.build();
    }
}
