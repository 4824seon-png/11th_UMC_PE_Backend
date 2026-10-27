// src/main/java/com/umc/movie/repository/RatingRepository.java
package com.umc.movie.repository;

import com.umc.movie.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByMember_MemberIdOrderByRatingIdDesc(Long memberId);


    // 스프링이 메서드 이름을 단어 단위로 쪼개서 SQL을 조립
//    find  By  Member_MemberId  OrderBy  RatingId  Desc
//────  ──  ──────────────  ───────  ────────  ────
//    조회   조건  member.memberId  정렬     ratingId  내림차순
}