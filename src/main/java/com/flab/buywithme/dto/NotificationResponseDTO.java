package com.flab.buywithme.dto;

import com.flab.buywithme.domain.Notification;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class NotificationResponseDTO {

    private int numberOfNotChecked;
    private List<Notification> notifications;
}
