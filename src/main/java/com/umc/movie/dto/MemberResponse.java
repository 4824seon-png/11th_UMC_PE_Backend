//회원가입 성공 시 클라이언트에게 돌려줄 회원 정보만 담는 응답 DTO입니다. 핵심은 password가 없다는 점

package com.umc.movie.dto;
// 엔티티를 그대로 반환하지 않는 이유
// Member를 그대로 반환하면 Jackson이 getter를 읽어 JSON을 만들기 때문에, 비밀번호 해시까지 응답에 나간다.
// 방향 : 서버 → 클라이언트
// 서버가 만든 값이므로 검증 애노테이션은 없음
public record MemberResponse(
        Long memberId,
        String email,
        String nickname
) {}