// src/main/java/com/umc/movie/service/RatingService.java
package com.umc.movie.service;

import com.umc.movie.dto.RatingResponse;
import com.umc.movie.repository.MemberRepository;
import com.umc.movie.repository.RatingRepository;
import com.umc.movie.dto.CreateRatingRequest;
import com.umc.movie.dto.UpdateRatingRequest;
import com.umc.movie.entity.Member;
import com.umc.movie.entity.Rating;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// 하나의 Service가 Repository 여러 개를 주입받아 쓸 수 있다 (회원 확인 + 평점 조회/등록/수정/삭제)
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
    @Transactional
    public Long create(Long memberId, CreateRatingRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        // rating 테이블에는 (member_id, movie_id) 복합 UNIQUE 제약이 걸려 있습니다.
        // 그냥 저장해도 DB가 막아주지만, 우리가 정한 메시지로 응답하기 위해 먼저 확인합니다.
        if (ratingRepository.existsByMember_MemberIdAndMovieId(memberId, request.movieId())) {
            throw new IllegalArgumentException("이미 이 영화에 평점을 남기셨습니다.");
        }

        Rating rating = Rating.builder()
                .member(member)
                .movieId(request.movieId())
                .score(request.score())
                .comment(request.comment())
                .build();

        return ratingRepository.save(rating).getRatingId();
    }

    @Transactional
    public void update(Long ratingId, Long memberId, UpdateRatingRequest request) {
        Rating rating = findOwnedRating(ratingId, memberId);

        rating.update(request.score(), request.comment());
        ratingRepository.save(rating);
    }

    @Transactional
    public void delete(Long ratingId, Long memberId) {
        Rating rating = findOwnedRating(ratingId, memberId);

        ratingRepository.delete(rating);
    }
// findOwnedRating은 평점을 찾고, 그 평점이 요청한 회원의 것인지까지 확인해서 돌려주는 메서드
    private Rating findOwnedRating(Long ratingId, Long memberId) {
        // 1) 평점 찾기: 없으면 404
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 평점입니다."));
       // 2) 평점의 작성자와 요청한 회원 비교: 다르면 403
        if (!rating.getMember().getMemberId().equals(memberId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인이 작성한 평점만 수정하거나 삭제할 수 있습니다.");
        }
        // 3) 두 검사를 통과한 평점만 반환
        return rating;
    }
}