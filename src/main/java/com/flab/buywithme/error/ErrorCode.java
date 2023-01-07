package com.flab.buywithme.error;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    /* 400 BAD_REQUEST : 잘못된 요청 */
    MEMBER_ALREADY_EXIST(BAD_REQUEST, "이미 존재하는 회원입니다"),
    MEMBER_NOT_FOUND(BAD_REQUEST, "잘못된 아이디 혹은 비밀번호입니다!"),

    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    UNAUTHORIZED_MEMBER(UNAUTHORIZED, "현재 내 계정 정보가 존재하지 않습니다"),

    /* 409 CONFLICT : 서버가 요청을 수행하는 중에 충돌 발생 */
    INVALID_UPDATE(CONFLICT, "업데이트 대상이 존재하지 않습니다");

    private final HttpStatus httpStatus;
    private final String message;
}
