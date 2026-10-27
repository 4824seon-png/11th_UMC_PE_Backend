// src/main/java/com/umc/movie/service/RatingService.java
package com.umc.movie.service;

import com.umc.movie.dto.RatingResponse;
import com.umc.movie.repository.MemberRepository;
import com.umc.movie.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// 하나의 Service가 Repository 여러 개를 주입받아 쓸 수 있다 (회원 존재 확인 + 평점 조회)
@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<RatingResponse> getRatingsByMember(Long memberId) {
        // 1) 회원 존재 확인 : JpaRepository 기본 메서드 existsById (직접 선언 안 해도 상속으로 제공)
        //    "회원 없음(404)"과 "평점 0개(200 + [])"를 구분하기 위해 먼저 검사
        if (!memberRepository.existsById(memberId)) {
            // ResponseStatusException : 던지면 Spring이 지정한 상태코드(404)로 응답. 아래 코드는 실행되지 않음
            // 6주차에 @RestControllerAdvice로 예외 처리를 한 곳에 모을 예정
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다.");
        }

        // 2) 평점 조회 → 3) Entity를 DTO로 변환
        //    stream() : 리스트를 하나씩 흘려보내는 통로
        //    map(RatingResponse::from) : 각 Rating을 RatingResponse로 변환 (= rating -> RatingResponse.from(rating))
        //    toList() : 다시 리스트로 모음
        //    변환을 Service(트랜잭션 안)에서 하는 이유 : open-in-view=false → 트랜잭션이 끝나면 영속성 컨텍스트가 닫힘
        return ratingRepository.findByMember_MemberIdOrderByRatingIdDesc(memberId).stream()
                .map(RatingResponse::from)
                .toList();
    }
}