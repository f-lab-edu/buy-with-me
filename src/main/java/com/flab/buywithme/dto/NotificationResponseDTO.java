package com.flab.buywithme.dto;

import com.flab.buywithme.domain.Notification;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class NotificationResponseDTO {

    private int numberOfNotChecked;
    private Page<Notification> notifications;
}
