package com.flab.buywithme.error;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    /* 400 BAD_REQUEST : 잘못된 요청 */
    INVALID_ID_PW(BAD_REQUEST, "잘못된 아이디 혹은 비밀번호입니다!"),

    /* 403 FORBIDDEN : 권한이 없는 요청  */
    IS_NOT_OWNER(UNAUTHORIZED, "작성자만 가능한 요청입니다"),

    /* 404 NOT_FOUND : 요청 대상이 존재하지 않을 때 */
    MEMBER_NOT_FOUND(NOT_FOUND, "존재하지 않는 멤버입니다"),
    POST_NOT_FOUND(NOT_FOUND, "해당 게시글이 존재하지 않습니다"),
    COMMENT_NOT_FOUND(NOT_FOUND, "해당 댓글이 존재하지 않습니다"),
    ENROLL_NOT_FOUND(NOT_FOUND, "등록 정보가 존재하지 않습니다"),
    NOTIFICATION_NOT_FOUND(NOT_FOUND, "해당 알림이 존재하지 않습니다"),
    EVALUATION_NOT_FOUND(NOT_FOUND, "해당 매너 평가가 존재하지 않습니다"),

    /* 409 CONFLICT : 서버의 상태와 충돌하는 경우 */
    MEMBER_ALREADY_EXIST(CONFLICT, "이미 존재하는 회원입니다"),
    GATHERING_FINISHED(CONFLICT, "인원 모집이 마감되었습니다"),
    ENROLL_ALREADY_DONE(CONFLICT, "이미 구매에 참여했습니다"),
    NOT_ENROLLED_MEMBER(CONFLICT, "해당 구매에 참여하지 않은 멤버입니다");

    private final HttpStatus httpStatus;
    private final String message;
}
