// 저장 API의 세 가지 문 중 두 번째 문 : 이미 있는 값인지
package com.umc.movie.service;

import com.umc.movie.dto.EmailCheckResponse;
import com.umc.movie.dto.NicknameCheckResponse;
import com.umc.movie.repository.MemberRepository;
import com.umc.movie.exception.DuplicateMemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.umc.movie.dto.MemberResponse;
import com.umc.movie.dto.SignUpRequest;
import com.umc.movie.entity.Member;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.umc.movie.dto.MemberInfoResponse;
import com.umc.movie.exception.ApiException;
import org.springframework.http.HttpStatus;
import com.umc.movie.dto.MemberUpdateRequest;

// [Service 계층] 비즈니스 판단(규칙) + 트랜잭션 담당. SQL은 모르고 Repository에게 "묻기만" 한다.
// @Service : Spring이 이 클래스를 Bean으로 만들어 관리 → Controller에 주입 가능
// @RequiredArgsConstructor : final 필드를 받는 생성자를 Lombok이 생성 → Spring이 생성자 주입(DI)
@Service
@RequiredArgsConstructor
public class MemberService {

    // final : 한 번 주입되면 바뀌지 않음 + 주입 누락 시 컴파일 단계에서 발견
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // readOnly = true : 조회 전용 트랜잭션 → 변경 감지 생략으로 가볍고, "데이터를 안 바꾼다"는 의도 표시
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
    @Transactional
    public MemberResponse signUp(SignUpRequest request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new DuplicateMemberException("MEMBER4091", "이미 사용 중인 이메일입니다.");
        }
        if (memberRepository.existsByNickname(request.nickname())) {
            throw new DuplicateMemberException("MEMBER4092", "이미 사용 중인 닉네임입니다.");
        }

        Member member = Member.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .build();

        Member savedMember = memberRepository.save(member);
        return new MemberResponse(savedMember.getMemberId(), savedMember.getEmail(), savedMember.getNickname());
    }

    public MemberInfoResponse getMyInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "MEMBER404_1",
                        "해당 회원을 찾을 수 없습니다."));

        return MemberInfoResponse.from(member);
    }

    public MemberInfoResponse updateMyInfo(Long memberId, MemberUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "MEMBER404_1",
                        "해당 회원을 찾을 수 없습니다."));

        // 나를 제외한 다른 회원이 그 닉네임을 쓰고 있다면 막습니다.
        // DB에도 UNIQUE 제약이 걸려 있지만 제약 위반 예외 대신 우리가 정한 응답을 주기 위해 먼저 확인합니다.
        if (memberRepository.existsByNicknameAndMemberIdNot(request.nickname(), memberId)) {
            throw new ApiException(HttpStatus.CONFLICT, "MEMBER409_1",
                    "이미 사용 중인 닉네임입니다.");
        }

        member.updateNickname(request.nickname());
        memberRepository.save(member);

        return MemberInfoResponse.from(member);
    }
}