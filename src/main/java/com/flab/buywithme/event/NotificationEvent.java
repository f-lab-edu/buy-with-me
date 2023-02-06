package com.flab.buywithme.event;

import com.flab.buywithme.domain.Member;
import com.flab.buywithme.domain.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class NotificationEvent {

    private Member member;
    private NotificationType notificationType;
}
