//이 회원이 이 영화에 이미 평점을 남겼나?를 DB에 물어보고 true/false로 답을 받는 메서드
package com.umc.movie.repository;

import com.umc.movie.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByMember_MemberIdOrderByRatingIdDesc(Long memberId);
    // _ : 안으로 들어가라는 표시
    // ~인 데이터가 있는지 확인, rating.member.memberId가 첫 번째 인자와 같고, 그리고, rating.movieId가 두 번째 인자와 같은가
    boolean existsByMember_MemberIdAndMovieId(Long memberId, Long movieId);

}