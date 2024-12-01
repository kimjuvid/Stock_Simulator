package com.stocksim.simulator.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    // 시크릿키 (application properties에 저장해 놓은 시크릿 키 가져와서 BASE64로 디코드)
//    @Value("${jwt.secret.key}")
//    private String secretKey;
//
//    @Bean
//    public SecretKey jwtSecretKey(){
//        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
//    }


    // 비크립트 DI방식으로 사용
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // csrf 켜기(csrf토큰 설정 - 활성화할 경우 사용)
    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-XSRF-TOKEN");
        return repository;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CSRF 비활성화 (필요 시 활성화 가능)
        http.csrf(csrf -> csrf.disable());

        // 권한 설정
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/favicon.ico").permitAll()  // favicon.ico 허용
                .requestMatchers("/css/**", "/img/**", "/js/**", "/lib/**", "/scss/**").permitAll()  // 정적 리소스 허용
                .requestMatchers("/login", "/register/**").permitAll() // 로그인 및 회원가입 허용
                .requestMatchers("/login/jwt").permitAll() // JWT 로그인 경로 허용
                .requestMatchers("/admin/**").hasRole("ADMIN") // 관리자만 접근 가능한 경로
                .requestMatchers("/api/**").hasAnyRole("USER", "ADMIN") // API 경로 인증
                .anyRequest().authenticated() // 나머지 요청은 인증 필요
        );


        // 로그아웃 설정
//        http.logout(logout -> logout
//                .logoutUrl("/logout") // `/logout` 경로로 로그아웃 처리
//                .permitAll() // 로그아웃 경로 접근 허용
//        );

        // JWT 기반 인증 설정
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 비활성화 (Stateless)
        );

        // JWT 필터 추가
        http.addFilterBefore(jwtFilter, ExceptionTranslationFilter.class);

        return http.build();
    }
}
