// src/main/java/com/umc/movie/service/RatingService.java
package com.umc.movie.service;

import com.umc.movie.dto.CreateRatingRequest;
import com.umc.movie.dto.RatingResponse;
import com.umc.movie.dto.UpdateRatingRequest;
import com.umc.movie.entity.Member;
import com.umc.movie.entity.Rating;
import com.umc.movie.exception.ApiException;
import com.umc.movie.repository.MemberRepository;
import com.umc.movie.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 하나의 Service가 Repository 여러 개를 주입받아 쓸 수 있다 (회원 확인 + 평점 조회/등록/수정/삭제)
// 7주차: memberId는 클라이언트가 아니라 토큰(인터셉터)이 알려준 값. "누가"가 항상 첫 번째 인자
@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final MemberRepository memberRepository;

    // 5주차 유저별 평점 조회 (공개 API). 예외만 ApiException으로 교체
    @Transactional(readOnly = true)
    public List<RatingResponse> getRatingsByMember(Long memberId) {
        // "회원 없음(404)"과 "평점 0개(200 + [])"를 구분하기 위해 먼저 검사
        if (!memberRepository.existsById(memberId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "MEMBER404_1",
                    "해당 회원을 찾을 수 없습니다.");
        }

        // open-in-view=false → 트랜잭션 안(Service)에서 Entity를 DTO로 변환
        return ratingRepository.findByMember_MemberIdOrderByRatingIdDesc(memberId).stream()
                .map(RatingResponse::from)
                .toList();
    }

    // 등록: 6주차는 ratingId(Long)만 반환 → 7주차는 RatingResponse 반환
    @Transactional
    public RatingResponse create(Long memberId, CreateRatingRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "MEMBER404_1",
                        "해당 회원을 찾을 수 없습니다."));

        // (member_id, movie_id) 복합 UNIQUE 제약이 있지만, 우리가 정한 코드로 응답하기 위해 먼저 확인
        if (ratingRepository.existsByMember_MemberIdAndMovieId(memberId, request.movieId())) {
            throw new ApiException(HttpStatus.CONFLICT, "RATING409_1",
                    "이미 이 영화에 평점을 남기셨습니다.");
        }

        Rating rating = Rating.builder()
                .member(member)
                .movieId(request.movieId())
                .score(request.score())
                .comment(request.comment())
                .build();

        return RatingResponse.from(ratingRepository.save(rating));
    }

    // 수정: 인자 순서 (ratingId, memberId) → (memberId, ratingId)
    @Transactional
    public RatingResponse update(Long memberId, Long ratingId, UpdateRatingRequest request) {
        Rating rating = findOwnedRating(memberId, ratingId);

        rating.update(request.score(), request.comment());
        ratingRepository.save(rating);

        return RatingResponse.from(rating);
    }

    // 삭제: 인자 순서 (ratingId, memberId) → (memberId, ratingId)
    @Transactional
    public void delete(Long memberId, Long ratingId) {
        Rating rating = findOwnedRating(memberId, ratingId);

        ratingRepository.delete(rating);
    }

    // 평점을 찾고(없으면 404), 요청자의 것인지 확인(아니면 403)해서 돌려준다
    // 인증은 인터셉터가, 인가(소유권)는 여기 Service가 담당
    private Rating findOwnedRating(Long memberId, Long ratingId) {
        // 1) 평점 찾기: 없으면 404
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "RATING404_1",
                        "해당 평점을 찾을 수 없습니다."));

        // 2) 작성자와 요청자 비교: 다르면 403 (토큰은 유효하지만 남의 것)
        if (!rating.getMember().getMemberId().equals(memberId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "RATING403_1",
                    "본인이 작성한 평점만 수정하거나 삭제할 수 있습니다.");
        }

        // 3) 두 검사를 통과한 평점만 반환
        return rating;
    }
}