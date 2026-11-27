// 회원 관련 HTTP 요청을 받는 입구
// 요청 값을 꺼내 MemberService에 넘기고, 결과를 HTTP 응답으로
// 중복 판단이나 저장 같은 업무 규칙은 Service 계층에 넣고, Controller에는 판단 로직을 넣지 않는다.
package com.umc.movie.controller;

import com.umc.movie.dto.EmailCheckResponse;
import com.umc.movie.dto.NicknameCheckResponse;
import com.umc.movie.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.umc.movie.dto.ApiResponse;
import com.umc.movie.dto.MemberResponse;
import com.umc.movie.dto.SignUpRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import com.umc.movie.dto.MemberInfoResponse;
import com.umc.movie.interceptor.AuthInterceptor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.umc.movie.dto.MemberUpdateRequest;
import com.umc.movie.config.SwaggerConfig;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

// [Controller 계층] HTTP의 입구. URL/메서드를 받고 → 값 꺼내서 → Service 호출 → 결과 반환. 판단 로직은 넣지 않는다.
// @RestController : 반환 객체를 Jackson이 JSON으로 변환해 응답 바디에 씀 (@Controller + @ResponseBody)
// @RequestMapping("/members") : 이 클래스의 모든 URL 앞에 /members가 공통으로 붙음
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "회원", description = "회원가입과 내 정보 API")
public class MemberController {

    private final MemberService memberService;
    // GET /members/nickname/{nickname}
    // {nickname} : 경로 변수 자리. @PathVariable이 이 자리 값을 꺼내 파라미터에 담음 (이름이 같아야 자동 연결)
    // Path를 쓰는 이유 : "이 닉네임"이라는 특정 대상 하나를 가리키므로
    @GetMapping("/nickname/{nickname}")

    public NicknameCheckResponse checkNickname(@PathVariable String nickname) {
        return memberService.checkNickname(nickname);
    }


    // GET /members/email/{email}
    @GetMapping("/email/{email}")
    public EmailCheckResponse checkEmail(@PathVariable String email) {
        return memberService.checkEmail(email);
    }

    @Operation(
            summary = "회원가입",
            description = "이메일, 비밀번호, 닉네임으로 회원을 등록합니다. 토큰이 없어도 호출할 수 있는 공개 API입니다."
    )

    @PostMapping
    public ResponseEntity<ApiResponse<MemberResponse>> signUp( // JSON 본문을 SignUpRequest로 변환
            @Valid @RequestBody SignUpRequest request // 변환된 값의 입력 규칙을 검사
    ) {
        MemberResponse response = memberService.signUp(request);
        return ResponseEntity.status(201)
                .body(ApiResponse.created("회원가입에 성공했습니다.", response));
    }
    @Operation(
            summary = "내 정보 조회",
            description = "토큰의 주인인 회원의 이메일, 닉네임, 가입일을 조회합니다."
    )
    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "토큰이 없거나 유효하지 않음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                        {
                          "isSuccess": false,
                          "code": "AUTH401_2",
                          "message": "로그인이 필요한 요청입니다.",
                          "result": null
                        }
                        """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "토큰은 유효하지만 회원이 존재하지 않음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                        {
                          "isSuccess": false,
                          "code": "MEMBER404_1",
                          "message": "해당 회원을 찾을 수 없습니다.",
                          "result": null
                        }
                        """)))
    })
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberInfoResponse>> getMyInfo(
            // 인터셉터가 request.setAttribute("memberId", ...)로 담아둔 값을 꺼냅니다
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId
    ) {
        MemberInfoResponse response = memberService.getMyInfo(memberId);
        return ResponseEntity.ok(ApiResponse.ok("회원 정보를 조회했습니다.", response));
    }
    @Operation(
            summary = "내 정보 수정",
            description = "토큰의 주인인 회원의 닉네임을 수정합니다. 다른 회원이 쓰고 있는 닉네임으로는 바꿀 수 없습니다.."
    )
    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "토큰이 없거나 유효하지 않음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                        {
                          "isSuccess": false,
                          "code": "AUTH401_2",
                          "message": "로그인이 필요한 요청입니다.",
                          "result": null
                        }
                        """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "토큰은 유효하지만 회원이 존재하지 않음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                        {
                          "isSuccess": false,
                          "code": "MEMBER404_1",
                          "message": "해당 회원을 찾을 수 없습니다.",
                          "result": null
                        }
                        """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409", description = "다른 회원이 사용 중인 닉네임",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                        {
                          "isSuccess": false,
                          "code": "MEMBER409_1",
                          "message": "이미 사용 중인 닉네임입니다.",
                          "result": null
                        }
                        """)))
    })
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<MemberInfoResponse>> updateMyInfo(
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @RequestBody @Valid MemberUpdateRequest request
    ) {
        MemberInfoResponse response = memberService.updateMyInfo(memberId, request);
        return ResponseEntity.ok(ApiResponse.ok("회원 정보를 수정했습니다.", response));
    }
}