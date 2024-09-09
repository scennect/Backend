package com.example.apiPayload.code.status;

import com.example.apiPayload.code.BaseCode;
import com.example.apiPayload.code.ReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // User Error
    USER_NOT_LOGIN(HttpStatus.BAD_REQUEST, "USER400", "로그인 하지 않았습니다."),
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER4001", "이미 존재하는 아이디입니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER4002", "사용자가 없습니다."),
    USER_NOT_FOUND_BY_EMAIL(HttpStatus.BAD_REQUEST, "USER4003", "이메일 사용자가 없습니다."),

    // Node Error
    NODE_NOT_FOUND(HttpStatus.BAD_REQUEST, "NODE4001", "노드가 없습니다."),
    NODE_NOT_CORRECT(HttpStatus.BAD_REQUEST, "NODE4002", "노드가 올바르지 않습니다."),
    NODE_INVALID_USER(HttpStatus.BAD_REQUEST, "NODE4003", "노드 삭제 권한이 없습니다."),

    // Project Error
    PROJECT_NOT_FOUND(HttpStatus.BAD_REQUEST, "PROJECT4001", "프로젝트가 없습니다."),
    PROJECT_NOT_PUBLIC(HttpStatus.BAD_REQUEST, "PROJECT4002", "공개된 프로젝트가 아닙니다."),

    // ProjectUser Error
    PROJECT_USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "PROJECT_USER4001", "프로젝트 사용자가 아닙니다."),

    //image Error
    IMAGE_GENERATE_FAILURE(HttpStatus.BAD_REQUEST, "IMAGE4001", "이미지 생성에 실패했습니다."),
    IMAGE_UPLOAD_FAILURE(HttpStatus.BAD_REQUEST, "IMAGE4002", "이미지 업로드에 실패했습니다."),
    PARENT_IMAGE_URL_NOT_CORRECT(HttpStatus.BAD_REQUEST, "IMAGE4003", "부모 이미지 URL이 올바르지 않습니다."),

    // Token Error
    ACCESS_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "TOKEN4001", "Access 토큰이 없습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "TOKEN4002", "Access 토큰이 유효하지 않습니다."),

    REFRESH_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "TOKEN4003", "Refresh 토큰이 없습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "TOKEN4004", "Refresh 토큰이 유효하지 않습니다."),

    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "TOKEN4005", "토큰이 만료되었습니다."),

    // Redis Error
    REDIS_NOT_FOUND(HttpStatus.BAD_REQUEST, "REDIS4001", "Redis 설정에 오류가 발생했습니다.");



    // Follow Error
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDto getReason() {
        return ReasonDto.builder()
                .httpStatus(httpStatus)
                .isSuccess(false)
                .code(this.code)
                .message(this.message)
                .build();
    }

}
