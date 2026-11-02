// src/main/java/com/umc/movie/entity/Member.java
package com.umc.movie.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(nullable = false, length = 60)
    private String password;

    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    // @Builder 객체를 값의 이름을 붙여가며 만들 수 있게 해 주는 코드를 Lombok이 컴파일할 때 자동으로 생성
    // 생성자 : 순서로 값을 구분, 빌더 : 이름으로 값을 구분
    // @Builder는 클래스에도 붙일 수 있지만, 그러면 모든 필드가 빌더에 포함되므로 메모리 소모가 크고 성능이 떨어질 수 있음
    // 생성자에 붙이면 그 생성자의 파라미터만 빌더에 들어감
    @Builder
    public Member(String email, String password, String nickname) {
        // 세 값이 모두 String이라 new Member(a, b, c)는 순서가 바뀌어도 컴파일 오류 X
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }
}