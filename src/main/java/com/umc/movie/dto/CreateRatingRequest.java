package com.umc.movie.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

// 2DTO의 차이는 movieId가 있느냐 하나뿐
// 평점을 새로 만들 때는 서버가 어떤 영화의 평점인지 알 수 없어서, 요청에서 받아야 한다.

public record CreateRatingRequest(
        @Schema(description = "TMDB 영화 ID", example = "550")
        @NotNull(message = "영화 ID를 입력해주세요.")
        @Positive(message = "영화 ID는 양수여야 합니다.")
        Long movieId,
        @Schema(description = "별점 (1~5)", example = "5")
        @NotNull(message = "별점을 입력해주세요.")
        @Min(value = 1, message = "별점은 1점 이상이어야 합니다.")
        @Max(value = 5, message = "별점은 5점 이하여야 합니다.")
        Integer score,
        @Schema(description = "후기 (선택, 500자 이하)", example = "인생 영화입니다")
        @Size(max = 500, message = "후기는 500자 이하로 입력해주세요.")
        String comment
) {}