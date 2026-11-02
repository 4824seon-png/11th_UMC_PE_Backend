// src/main/java/com/umc/movie/controller/RatingController.java
package com.umc.movie.controller;

import com.umc.movie.dto.RatingResponse;
import com.umc.movie.service.RatingService;
import com.umc.movie.dto.ApiResponse;
import com.umc.movie.dto.CreateRatingRequest;
import com.umc.movie.dto.RatingResponse;
import com.umc.movie.dto.UpdateRatingRequest;
import com.umc.movie.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
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

//① 어떤 요청을 받을지 지정 → ② 요청에서 값 꺼내기 → ③ Service 호출 → ④ 응답 반환
public class RatingController {

    private final RatingService ratingService;

    // GET /members/{memberId}/ratings
    // @PathVariable Long memberId : 경로의 문자열 "1"을 Long 1로 자동 변환. 숫자가 아니면(abc) 400 Bad Request
    // 반환 List<RatingResponse> → JSON 배열 [ {...}, {...} ]
    @GetMapping("/{memberId}/ratings")
    public List<RatingResponse> getMemberRatings(@PathVariable Long memberId) {
        return ratingService.getRatingsByMember(memberId);
    }
// 등록: create
    @PostMapping("/{memberId}/ratings")
    public ResponseEntity<ApiResponse<Long>> create(
            @PathVariable Long memberId,
            @Valid @RequestBody CreateRatingRequest request
    ) {
        Long ratingId = ratingService.create(memberId, request);
        return ResponseEntity.status(201).body(ApiResponse.created("평점 등록에 성공했습니다.", ratingId));
    }
// 수정: update
    // 어떤 평점을 고칠지 알아야 해서 주소에 {ratingId}가 추가
    // 돌려줄 데이터는 없어서 ApiResponse<Void>이고 result는 null
    @PatchMapping("/{memberId}/ratings/{ratingId}")
    public ResponseEntity<ApiResponse<Void>> update(
            @PathVariable Long memberId,
            @PathVariable Long ratingId,
            @Valid @RequestBody UpdateRatingRequest request
    ) {
        ratingService.update(ratingId, memberId, request);
        return ResponseEntity.ok(ApiResponse.ok("평점 수정에 성공했습니다.", null));
    }
// 삭제: delete
    // 삭제에는 JSON이 필요 없어서 @RequestBody가 없다. 삭제 성공 시 204 No Content를 반환하고, 본문은 비워둔다.
    @DeleteMapping("/{memberId}/ratings/{ratingId}")
    public ResponseEntity<Void> delete(@PathVariable Long memberId, @PathVariable Long ratingId) {
        ratingService.delete(ratingId, memberId);
        return ResponseEntity.noContent().build();
    }
}