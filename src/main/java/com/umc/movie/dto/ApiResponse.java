//성공과 실패 응답을 같은 네 칸(isSuccess, code, message, result)으로 맞추는 공통 틀
package com.umc.movie.dto;
// 제네릭 <T>, result에 들어갈 타입을 쓰는 곳에서 정한다

// static은 클래스의 T를 쓸 수 없어 메서드에 <T>를 따로 선언
// 정적 팩토리 : 성공 여부와 코드를 고정해 호출을 간단하게 함
public record ApiResponse<T>(boolean isSuccess, String code, String message, T result) {
    public static <T> ApiResponse<T> created(String message, T result) {
        //1. 매서드에서 T를 쓰겠다는 선언 2. ApiResponse<T> 반환 타입 3. T result 넘긴 값의 타입으로 T가 정해진다.
        return new ApiResponse<>(true, "COMMON201", message, result);
    }

    public static <T> ApiResponse<T> ok(String message, T result) {
        return new ApiResponse<>(true, "COMMON200", message, result);
    }

    public static ApiResponse<Void> failure(String code, String message) {
        return new ApiResponse<>(false, code, message, null);
    }
}
