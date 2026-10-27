// src/main/java/com/umc/movie/repository/MemberRepository.java
package com.umc.movie.repository;

import com.umc.movie.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

//서버 시작 → Spring Data JPA가 "JpaRepository를 상속한 인터페이스"를 발견
//         → 실행 중에 구현 객체(프록시)를 자동으로 만들어 Bean으로 등록
//         → Service가 생성자로 주입받아 사용
//그래서 @Repository 어노테이션도 붙이지 않아도 된다. 상속만으로 Spring이 알아봄
public interface MemberRepository extends JpaRepository<Member, Long> {
    // <엔티티, 엔티티의 @Id 필드타입>
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);
}