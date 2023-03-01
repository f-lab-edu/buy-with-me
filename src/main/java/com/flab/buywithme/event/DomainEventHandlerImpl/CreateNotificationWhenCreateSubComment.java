package com.flab.buywithme.event.DomainEventHandlerImpl;

import com.flab.buywithme.domain.Notification;
import com.flab.buywithme.domain.PostComment;
import com.flab.buywithme.event.DomainEvent;
import com.flab.buywithme.event.DomainEventHandler;
import com.flab.buywithme.event.DomainEventType;
import com.flab.buywithme.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CreateNotificationWhenCreateSubComment implements DomainEventHandler<PostComment> {

    private final NotificationRepository notificationRepository;

    @Override
    public boolean canHandle(DomainEvent<PostComment> event) {
        return event.getDomainEventType() == DomainEventType.CREATE_SUB_COMMENT;
    }

    @Override
    @Transactional
    public void handle(DomainEvent<PostComment> event) {
        PostComment comment = event.getSource();

        Notification notification = Notification.builder()
                .member(comment.getMember())
                .checked(false)
                .domainEventType(event.getDomainEventType())
                .build();
        notificationRepository.save(notification);
    }
}
