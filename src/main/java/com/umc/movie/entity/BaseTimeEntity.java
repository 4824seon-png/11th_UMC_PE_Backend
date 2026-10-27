package com.umc.movie.entity;
// BaseTimeEntity: 공통 시간 컬럼을 물려주는 부모
//member, rating, favorite 세 테이블 모두 created_at, updated_at을 가지고 있음
//매번 복사해서 쓰는 대신 부모 클래스 하나에 쓰고 상속

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
//테이블은 아니지만 자식에게 컬럼 정의를 물려줌
public abstract class BaseTimeEntity {
// new BaseTimeEntity() 금지, 어쩌피 혼자서는 의미없는 클래스
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
