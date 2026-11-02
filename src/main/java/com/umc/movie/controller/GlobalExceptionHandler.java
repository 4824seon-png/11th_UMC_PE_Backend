// 전역 예외 처리기
package com.umc.movie.controller;

import com.umc.movie.dto.ApiResponse;
import com.umc.movie.exception.DuplicateMemberException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
// 모든 Controller에서 나온 예외를 이 클래스가 받고, 반환값을 JSON으로 응답
public class GlobalExceptionHandler {
    // @ExceptionHandler(X.class) : X 예외가 발생하면 이 메서드를 실행
    // 잡는 상황
    // @Valid 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest().body(ApiResponse.failure("COMMON4001", message));
    }

    // UNIQUE 등 DB 제약 위반
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException exception) {
        return ResponseEntity.status(409).body(ApiResponse.failure("COMMON4091", "이미 존재하는 데이터입니다."));
    }

    // Service의 중복 판단
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException exception) {
        return ResponseEntity.status(409).body(ApiResponse.failure("COMMON4092", exception.getMessage()));
    }

    // 없는 평점, 권한 없음 등
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleResponseStatus(ResponseStatusException exception) {
        int status = exception.getStatusCode().value();
        return ResponseEntity.status(status)
                .body(ApiResponse.failure("COMMON" + status, exception.getReason()));
    }

    // 회원가입 이메일, 닉네임 중복
    @ExceptionHandler(DuplicateMemberException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateMember(DuplicateMemberException exception) {
        return ResponseEntity.status(409)
                .body(ApiResponse.failure(exception.getCode(), exception.getMessage()));
    }
}