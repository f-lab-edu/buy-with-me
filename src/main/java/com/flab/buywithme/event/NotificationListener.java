package com.flab.buywithme.event;

import com.flab.buywithme.domain.Member;
import com.flab.buywithme.domain.Notification;
import com.flab.buywithme.domain.enums.NotificationType;
import com.flab.buywithme.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Async
@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleNotificationCreateRequest(NotificationEvent notificationEvent) {
        createNotification(notificationEvent.getMember(),
                notificationEvent.getNotificationType());
    }

    private void createNotification(Member member, NotificationType notificationType) {
        Notification notification = Notification.builder()
                .member(member)
                .checked(false)
                .notificationType(notificationType)
                .build();
        notificationRepository.save(notification);
    }
}
