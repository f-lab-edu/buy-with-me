package com.flab.buywithme.error;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    /* 400 BAD_REQUEST : 잘못된 요청 */
    MEMBER_ALREADY_EXIST(BAD_REQUEST, "이미 존재하는 회원입니다"),
    INVALID_ID_PW(BAD_REQUEST, "잘못된 아이디 혹은 비밀번호입니다!"),
    IS_NOT_OWNER(BAD_REQUEST, "작성자만 가능한 요청입니다"),

    /* 404 NOT_FOUND : 요청 대상이 존재하지 않을 때 */
    MEMBER_NOT_FOUND(NOT_FOUND, "존재하지 않는 멤버입니다"),
    POST_NOT_FOUND(NOT_FOUND, "해당 게시글이 존재하지 않습니다");

    private final HttpStatus httpStatus;
    private final String message;
}
