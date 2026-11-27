package com.umc.movie.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateRatingRequest(
        @Schema(description = "별점 (1~5)", example = "5")
        @NotNull(message = "별점을 입력해주세요.")
        @Min(value = 1, message = "별점은 1점 이상이어야 합니다.")
        @Max(value = 5, message = "별점은 5점 이하여야 합니다.")
        Integer score,
        @Schema(description = "후기 (선택, 500자 이하)", example = "인생 영화입니다")
        @Size(max = 500, message = "후기는 500자 이하로 입력해주세요.")
        String comment
) {}