// src/main/java/com/umc/movie/controller/RatingController.java
package com.umc.movie.controller;

import com.umc.movie.dto.ApiResponse;
import com.umc.movie.dto.CreateRatingRequest;
import com.umc.movie.dto.RatingResponse;
import com.umc.movie.dto.UpdateRatingRequest;
import com.umc.movie.interceptor.AuthInterceptor;
import com.umc.movie.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.umc.movie.config.SwaggerConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

// 7주차: 클래스 레벨 @RequestMapping("/members") 제거
// → 남아 있으면 /members/ratings 가 되어 404. 대신 메서드마다 전체 경로를 적는다
@RestController
@RequiredArgsConstructor
@Tag(name = "평점", description = "영화 평점 등록·수정·삭제와 회원별 평점 조회 API")
public class RatingController {

    private final RatingService ratingService;

    // GET /members/{memberId}/ratings : 5주차 공개 조회. 토큰 불필요 (인터셉터 exclude 목록)
    @Operation(
            summary = "회원별 평점 조회",
            description = "특정 회원이 남긴 평점 목록을 최신순으로 조회합니다. 토큰 없이 누구나 볼 수 있습니다. 평점이 없으면 빈 배열을 돌려줍니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "조회 성공 (평점이 없으면 빈 배열)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "존재하지 않는 회원",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                        {
                          "isSuccess": false,
                          "code": "MEMBER404_1",
                          "message": "해당 회원을 찾을 수 없습니다.",
                          "result": null
                        }
                        """)))
    })
    @GetMapping("/members/{memberId}/ratings")
    public List<RatingResponse> getMemberRatings(@PathVariable Long memberId) {
        return ratingService.getRatingsByMember(memberId);
    }

    // POST /ratings : URL에서 /members/{memberId} 제거 → memberId는 인터셉터가 담아둔 값
    @Operation(
            summary = "평점 등록",
            description = "토큰의 주인인 회원이 TMDB 영화 하나에 별점(1~5)과 후기를 남깁니다. 같은 영화에는 한 번만 남길 수 있습니다."
    )
    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201", description = "평점 등록 성공"),
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
                    responseCode = "409", description = "이미 평점을 남긴 영화",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                        {
                          "isSuccess": false,
                          "code": "RATING409_1",
                          "message": "이미 이 영화에 평점을 남기셨습니다.",
                          "result": null
                        }
                        """)))
    })
    @PostMapping("/ratings")
    public ResponseEntity<ApiResponse<RatingResponse>> create(
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @RequestBody @Valid CreateRatingRequest request
    ) {
        RatingResponse response = ratingService.create(memberId, request);
        return ResponseEntity.status(201)
                .body(ApiResponse.created("평점을 등록했습니다.", response));
    }

    // PATCH /ratings/{ratingId} : @PathVariable memberId 제거
    @Operation(
            summary = "평점 수정",
            description = "평점의 별점과 후기를 수정합니다. 본인이 작성한 평점만 수정할 수 있습니다."
    )
    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "평점 수정 성공"),
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
                    responseCode = "403", description = "다른 회원의 평점",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                        {
                          "isSuccess": false,
                          "code": "RATING403_1",
                          "message": "본인이 작성한 평점만 수정하거나 삭제할 수 있습니다.",
                          "result": null
                        }
                        """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "존재하지 않는 평점",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                        {
                          "isSuccess": false,
                          "code": "RATING404_1",
                          "message": "해당 평점을 찾을 수 없습니다.",
                          "result": null
                        }
                        """)))
    })
    @PatchMapping("/ratings/{ratingId}")
    public ResponseEntity<ApiResponse<RatingResponse>> update(
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @PathVariable Long ratingId,
            @RequestBody @Valid UpdateRatingRequest request
    ) {
        RatingResponse response = ratingService.update(memberId, ratingId, request);
        return ResponseEntity.ok(ApiResponse.ok("평점을 수정했습니다.", response));
    }

    // DELETE /ratings/{ratingId} : 204 → 200 + result null (공통 응답 형식 유지)
    @Operation(
            summary = "평점 삭제",
            description = "평점을 삭제합니다. 본인이 작성한 평점만 삭제할 수 있습니다."
    )
    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "평점 삭제 성공"),
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
                    responseCode = "403", description = "다른 회원의 평점",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                        {
                          "isSuccess": false,
                          "code": "RATING403_1",
                          "message": "본인이 작성한 평점만 수정하거나 삭제할 수 있습니다.",
                          "result": null
                        }
                        """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "존재하지 않는 평점",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                        {
                          "isSuccess": false,
                          "code": "RATING404_1",
                          "message": "해당 평점을 찾을 수 없습니다.",
                          "result": null
                        }
                        """)))
    })
    @DeleteMapping("/ratings/{ratingId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @PathVariable Long ratingId
    ) {
        ratingService.delete(memberId, ratingId);
        return ResponseEntity.ok(ApiResponse.ok("평점을 삭제했습니다.", null));
    }
}