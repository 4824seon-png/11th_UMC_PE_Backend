// src/main/java/com/umc/movie/controller/MemberController.java
package com.umc.movie.controller;

import com.umc.movie.dto.EmailCheckResponse;
import com.umc.movie.dto.NicknameCheckResponse;
import com.umc.movie.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

// [Controller 계층] HTTP의 입구. URL/메서드를 받고 → 값 꺼내서 → Service 호출 → 결과 반환. 판단 로직은 넣지 않는다.
// @RestController : 반환 객체를 Jackson이 JSON으로 변환해 응답 바디에 씀 (@Controller + @ResponseBody)
// @RequestMapping("/members") : 이 클래스의 모든 URL 앞에 /members가 공통으로 붙음
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
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
}