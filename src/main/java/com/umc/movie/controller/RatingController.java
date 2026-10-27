// src/main/java/com/umc/movie/controller/RatingController.java
package com.umc.movie.controller;

import com.umc.movie.dto.RatingResponse;
import com.umc.movie.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// MemberController와 같은 /members를 써도 괜찮다.
// 파일은 "다루는 자원(Rating)" 기준으로 나누고, URL은 프론트가 이해하기 쉬운 모양(회원의 평점 = /members/{id}/ratings)으로 맞춘다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class RatingController {

    private final RatingService ratingService;

    // GET /members/{memberId}/ratings
    // @PathVariable Long memberId : 경로의 문자열 "1"을 Long 1로 자동 변환. 숫자가 아니면(abc) 400 Bad Request
    // 반환 List<RatingResponse> → JSON 배열 [ {...}, {...} ]
    @GetMapping("/{memberId}/ratings")
    public List<RatingResponse> getMemberRatings(@PathVariable Long memberId) {
        return ratingService.getRatingsByMember(memberId);
    }
}