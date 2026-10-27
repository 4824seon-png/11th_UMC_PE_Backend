// src/main/java/com/umc/movie/service/MemberService.java
package com.umc.movie.service;

import com.umc.movie.dto.EmailCheckResponse;
import com.umc.movie.dto.NicknameCheckResponse;
import com.umc.movie.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// [Service 계층] 비즈니스 판단(규칙) + 트랜잭션 담당. SQL은 모르고 Repository에게 "묻기만" 한다.
// @Service : Spring이 이 클래스를 Bean으로 만들어 관리 → Controller에 주입 가능
// @RequiredArgsConstructor : final 필드를 받는 생성자를 Lombok이 생성 → Spring이 생성자 주입(DI)
@Service
@RequiredArgsConstructor
public class MemberService {

    // final : 한 번 주입되면 바뀌지 않음 + 주입 누락 시 컴파일 단계에서 발견
    private final MemberRepository memberRepository;

    // readOnly = true : 조회 전용 트랜잭션 → 변경 감지 생략으로 가볍고, "데이터를 안 바꾼다"는 의도 표시
    // import 주의 : org.springframework.transaction.annotation.Transactional (jakarta 것은 readOnly 없음)
    @Transactional(readOnly = true)
    public NicknameCheckResponse checkNickname(String nickname) {
        // existsBy : 1건을 찾는 즉시 멈춤(LIMIT 1) → findBy().isPresent()보다 가벼움
        boolean exists = memberRepository.existsByNickname(nickname);
        // DB 관점은 "존재하나?(exists)", 화면 관점은 "쓸 수 있나?(available)" → 뒤집어서(!) 화면 언어로 번역
        return new NicknameCheckResponse(!exists);
    }

    @Transactional(readOnly = true)
    public EmailCheckResponse checkEmail(String email) {
        boolean exists = memberRepository.existsByEmail(email);
        return new EmailCheckResponse(!exists);
    }
}