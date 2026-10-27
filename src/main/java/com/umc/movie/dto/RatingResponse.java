// src/main/java/com/umc/movie/dto/RatingResponse.java
package com.umc.movie.dto;

import com.umc.movie.entity.Rating;

import java.time.LocalDateTime;

public record RatingResponse(
        Long ratingId,
        Long movieId,
        Integer score,
        String comment,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    // 정적 팩토리 메서드 static 객체 없이 RatingResponse.from(rating)으로 클래스 이름으로 바로 호출
    // 이 메서드를 사용하면, Rating 객체를 RatingResponse 객체로 쉽게 변환할 수 있다.
    // 엔티티 → DTO 변환 방법을 DTO 안에 모아 둔 것으로. 변환 규칙이 바뀌면 이 메서드 하나만 고치면 된다.
    public static RatingResponse from(Rating rating) {
        return new RatingResponse(
                rating.getRatingId(),
                rating.getMovieId(),
                rating.getScore(),
                rating.getComment(),
                rating.getCreatedAt(),
                rating.getUpdatedAt()
        );
    }
}
