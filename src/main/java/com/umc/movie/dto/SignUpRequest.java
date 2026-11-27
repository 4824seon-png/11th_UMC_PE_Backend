//POST /members로 들어온 JSON을 담고, 입력 규칙까지 함께 선언하는 요청 DTO
//JSON 요청 → Jackson이 SignUpRequest로 변환 → @Valid 검증
//  ├ 통과 → Controller → Service
//  └ 실패 → MethodArgumentNotValidException → 전역 핸들러 → 400
package com.umc.movie.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public record SignUpRequest(
        // record는 필드, 생성자, 접근자(email()), equals, toString을 자동으로 만들어줌, 값을 바꿀 수 없다 dto에 적합

        @Schema(description = "로그인에 사용할 이메일", example = "challenger@umc.com")
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @Schema(description = "대문자/소문자/숫자/특수문자를 포함한 8자 이상", example = "Umc1234!")
        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,}$",
                message = "비밀번호는 대문자/소문자/숫자/특수문자를 포함한 8자 이상이어야 합니다."
        )
        String password,

        @Schema(description = "2자 이상 12자 이하의 닉네임", example = "영화조아")
        @NotBlank(message = "닉네임을 입력해주세요.")
        @Size(min = 2, max = 12, message = "닉네임은 2자 이상 12자 이하로 입력해주세요.")
        String nickname
) {}
