// src/main/java/com/umc/movie/dto/NicknameCheckResponse.java


// DTO 계층 사이나 서버와 클라이언트 사이에서 데이터를 옮기기만 하는 상자. 로직은 없고, 값만 담는다.
// @RestController가 객체를 반환하면, Spring Boot에 들어 있는 Jackson 라이브러리가 객체의 getter(또는 record 접근자)를
// 읽어서 JSON을 만든다. [DTO의 필드 이름이 곧 JSON 키 이름]
// 엔티티는 DB용, DTO는 화면용

package com.umc.movie.dto;

public record NicknameCheckResponse(boolean available) {}