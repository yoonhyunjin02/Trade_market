package com.owl.trade_market.config;

import com.owl.trade_market.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web
        .builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 웹 보안 기능 활성화
public class SecurityConfig {

    // 기존 PasswordEncoder 빈
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    private final CustomOAuth2UserService customOAuth2UserService;

    // 구글·카카오 등 OAuth2 공급자로부터 받은 프로필 정보를 DB에 저장하거나 세션에 올릴 때 이 클래스가 사용됨
    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
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
                                "/login", "/users/login",
                                "/login/oauth2/**"   // 콜백 경로 허용
                        ).permitAll()
                        // 그 외에는 모두 인증 필요
                        .anyRequest().authenticated()
                )
                // 2) OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")  // 커스텀 로그인 페이지
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)  // // 로그인 성공 후 사용자 정보 처리 담당
                        )
                        .defaultSuccessUrl("/main", true)  // 로그인 성공 시 무조건 “/main”로 리다이렉트
                        .failureUrl("/login?error=oauth2_error")  // 로그인 실패 시 에러 파라미터를 달아 다시 로그인 페이지로
                )

                // 3) 커스텀 로그인 페이지 설정
                .formLogin(form -> form
                        .loginPage("/login")      // 내가 만든 /login 컨트롤러/뷰 사용
                        .usernameParameter("userId")    // 커스텀 username사용(기본: username)
                        .passwordParameter("userPassword") // 커스텀 password사용(기본: password)
                        .loginProcessingUrl("/users/login") // form action URL
                        .failureUrl("/login?error") //추가
                        .defaultSuccessUrl("/main", false)   // 로그인 성공 후 리다이렉트
                        .permitAll()
                )
                // 3) 로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/main")
                        .permitAll()
                );

        return http.build();
    }
}
