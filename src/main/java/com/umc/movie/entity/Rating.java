// src/main/java/com/umc/movie/entity/Rating.java
package com.umc.movie.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "rating", uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "movie_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Rating extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_id")
    private Long ratingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
// 우리 DB의 member 테이블, FOREIGN KEY
// 참조할 엔티티가 우리 코드에 있으면 객체로, 없으면 값으로
    @Column(name = "movie_id", nullable = false)
    private Long movieId;
// TMDB의 영화 (우리 DB에 없음)
    @Column(nullable = false, columnDefinition = "TINYINT")
    private Integer score;

    @Column(columnDefinition = "TEXT")
    private String comment;

    //@Builder 생성자: 새 평점 만들기
    @Builder
    public Rating(Member member, Long movieId, Integer score, String comment) {
        this.member = member;
        this.movieId = movieId;
        this.score = score;
        this.comment = comment;
    }
    // update(): 있는 평점 고치기
    // setter가 있다면: 영화까지 어디서든 바꿀 수 있음
    public void update(Integer score, String comment) {
        this.score = score;
        this.comment = comment;
    }
}