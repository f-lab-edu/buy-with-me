package com.flab.buywithme.event.DomainEventHandlerImpl;

import com.flab.buywithme.domain.Notification;
import com.flab.buywithme.domain.Post;
import com.flab.buywithme.event.DomainEvent;
import com.flab.buywithme.event.DomainEventHandler;
import com.flab.buywithme.event.DomainEventType;
import com.flab.buywithme.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CreateNotificationWhenCreateComment implements DomainEventHandler<Post> {

    private final NotificationRepository notificationRepository;

    @Override
    public boolean canHandle(DomainEvent<Post> event) {
        return event.getDomainEventType() == DomainEventType.CREATE_COMMENT;
    }

    @Override
    @Transactional
    public void handle(DomainEvent<Post> event) {
        Post post = event.getSource();

        Notification notification = Notification.builder()
                .member(post.getMember())
                .checked(false)
                .domainEventType(event.getDomainEventType())
                .build();
        notificationRepository.save(notification);
    }
}
