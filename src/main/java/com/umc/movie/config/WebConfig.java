// src/main/java/com/umc/movie/config/WebConfig.java
package com.umc.movie.config;

import com.umc.movie.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// [CORS 전역 설정]
// CORS : 서버가 응답 헤더(Access-Control-Allow-Origin)로 "이 출처는 내 응답을 읽어도 된다"고 브라우저에 허락하는 약속
// SOP  : 브라우저가 다른 출처(프로토콜+호스트+포트)의 응답을 JS가 읽지 못하게 막는 기본 규칙
//        → 5173(프론트)과 8080(백엔드)은 포트가 달라 다른 출처 → 허락 헤더가 없으면 브라우저가 응답을 버림
// CORS 에러의 원인은 항상 서버 설정 → 프론트 코드는 고칠 필요 없음
//
// @Configuration : 이 클래스가 Spring 설정 클래스임을 표시 → 서버 시작 시 읽어서 적용
// WebMvcConfigurer : Spring MVC 기본 설정에 "내 설정을 덧붙이는" 인터페이스 (필요한 메서드만 골라서 override)
// 컨트롤러마다 @CrossOrigin을 붙일 수도 있지만, 서비스 전체에 같은 정책이면 한 곳에서 관리하는 게 유지보수에 유리
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")                                   // 모든 URL 경로에 이 정책 적용 (** = 하위 경로 전부)
                .allowedOrigins("http://localhost:5173")             // 이 출처에서 온 요청만 허용 → 응답에 Allow-Origin 헤더가 붙음
                .allowedMethods("GET", "POST", "PATCH", "DELETE")    // 허용 HTTP 메서드 (preflight 응답의 Allow-Methods)
                .allowedHeaders("*");                                // 허용 요청 헤더 (preflight 응답의 Allow-Headers, 7주차 Authorization 대비)
        // "*"(모든 출처)를 쓰지 않는 이유 : 아무 사이트의 JS나 우리 응답을 읽을 수 있게 됨 + allowCredentials(true)와 함께 쓸 수 없음
        // 배포 후에는 실제 프론트 도메인(예: https://movie.example.com)을 추가해야 함
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                // 일단 모든 경로를 잠그고
                .addPathPatterns("/**")
                // 로그인 없이도 써야 하는 경로만 예외로 열어줍니다
                .excludePathPatterns(
                        "/auth/**",              // 로그인
                        "/members",              // 회원가입 (POST /members)
                        "/members/nickname/**",  // 닉네임 중복 체크
                        "/members/email/**",     // 이메일 중복 체크
                        "/members/*/ratings",    // 유저별 평점 조회 (누구나 볼 수 있는 목록)
                        "/error"                 // 스프링이 내부적으로 쓰는 경로
                );
    }
}