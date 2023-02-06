package com.flab.buywithme.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/*
    COMMENT_ALERT: 게시글/댓글에 댓글이 달린 경우 작성자에게 알림
    TARGET_NUM_REACHED : 구매 참여 인원 모집이 완료되었을 때 모든 참여자에게 알림
    ENROLL_CANCELED : 게시글 작성자가 공동 구매 모집을 취소한 경우 작성자를 제외한 모든 참여자에게 알림
*/
@Getter
@RequiredArgsConstructor
public enum NotificationType {
    COMMENT_ALERT("댓글 알림"),
    TARGET_NUM_REACHED("참여한 공동 구매 인원 모집이 완료되었습니다."),
    ENROLL_CANCELED("참여한 공동 구매 인원 모집이 취소되었습니다.");

    private final String message;
}
