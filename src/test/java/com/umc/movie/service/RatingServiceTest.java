package com.umc.movie.service;

import com.umc.movie.dto.CreateRatingRequest;
import com.umc.movie.dto.UpdateRatingRequest;
import com.umc.movie.entity.Member;
import com.umc.movie.entity.Rating;
import com.umc.movie.exception.ApiException;
import com.umc.movie.repository.MemberRepository;
import com.umc.movie.repository.RatingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private RatingService ratingService;

    private final Member member = Member.builder()
            .email("challenger@umc.com")
            .password("암호화된비밀번호")
            .nickname("영화조아")
            .build();

    @Test
    @DisplayName("같은 영화에 평점을 다시 등록하면 RATING409_1 예외를 던지고 저장하지 않는다")
    void createRatingWhenDuplicated() {
        // given: 회원은 존재하고, 이 영화에 이미 평점을 남긴 상황
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(ratingRepository.existsByMember_MemberIdAndMovieId(1L, 550L)).willReturn(true);

        // when & then: 409 예외가 나고, 저장은 시도조차 하지 않는다
        assertThatThrownBy(() -> ratingService.create(1L, new CreateRatingRequest(550L, 5, "인생 영화입니다")))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("code", "RATING409_1")
                .hasMessage("이미 이 영화에 평점을 남기셨습니다.");

        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    @DisplayName("다른 회원의 평점을 수정하려 하면 RATING403_1 예외를 던지고 평점은 바뀌지 않는다")
    void updateOthersRating() {
        // given: 1번 회원이 쓴 평점(10번)이 있고, 2번 회원이 수정하려는 상황
        Member owner = Member.builder()
                .email("owner@umc.com")
                .password("암호화된비밀번호")
                .nickname("주인")
                .build();
        ReflectionTestUtils.setField(owner, "memberId", 1L); // DB가 채워줄 id를 테스트가 대신 넣습니다

        Rating rating = Rating.builder()
                .member(owner)
                .movieId(550L)
                .score(5)
                .comment("원래 후기")
                .build();
        given(ratingRepository.findById(10L)).willReturn(Optional.of(rating));

        // when & then: 403 예외가 나고
        assertThatThrownBy(() -> ratingService.update(2L, 10L, new UpdateRatingRequest(1, "몰래 수정")))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("code", "RATING403_1")
                .hasMessage("본인이 작성한 평점만 수정하거나 삭제할 수 있습니다.");

        // then: 평점 내용은 그대로이고, 저장도 호출되지 않는다
        assertThat(rating.getScore()).isEqualTo(5);
        assertThat(rating.getComment()).isEqualTo("원래 후기");
        verify(ratingRepository, never()).save(any(Rating.class));
    }
}