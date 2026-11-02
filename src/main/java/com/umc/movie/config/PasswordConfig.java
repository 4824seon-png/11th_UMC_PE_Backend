//비밀번호 해시 도구인 BCryptPasswordEncoder를 Spring Bean으로 등록하는 설정 파일

package com.umc.movie.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {
    // @Bean : 반환한 객체를 Spring이 관리 (라이브러리 클래스라 @Component를 붙일 수 없음)
    // @Bean은 객체를 만들어 반환하는 메서드에 붙인다.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
//        단방향 해시로 해시값에서 원래 비밀번호를 되돌릴 수 없다(원문 복원을 전재하지 않는다). 암호화는 키가 있으면 복호화 가능
//        총 60자 그래서 DB의 password 컬럼이 VARCHAR(60)
    }
}
