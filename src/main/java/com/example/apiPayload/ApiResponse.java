package com.example.apiPayload;


import com.example.apiPayload.code.status.SuccessStatus;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "data"})
public class ApiResponse<T> {


    private final Boolean isSuccess;
    private final String code;
    private final String message;
    private T data;

    // 성공한 경우 응답 생성
    public static <T> ApiResponse<T> onSuccess(String code, String message, T data){
        return new ApiResponse<>(true, code, message, data);
    }

    //실패한 경우 응답 생성
    public static <T> ApiResponse<T> onFailure(String code, String message, T data ){
        return new ApiResponse<>(false, code, message, data);
    }
}
