package com.flab.buywithme.event.DomainEventHandlerImpl;

import com.flab.buywithme.domain.Enroll;
import com.flab.buywithme.domain.Notification;
import com.flab.buywithme.domain.Post;
import com.flab.buywithme.event.DomainEvent;
import com.flab.buywithme.event.DomainEventHandler;
import com.flab.buywithme.event.DomainEventType;
import com.flab.buywithme.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CreateNotificationWhenDeletePost implements DomainEventHandler<Post> {

    private final NotificationRepository notificationRepository;

    @Override
    public boolean canHandle(DomainEvent<Post> event) {
        return event.getDomainEventType() == DomainEventType.DELETE_POST;
    }

    @Override
    @Transactional
    public void handle(DomainEvent<Post> event) {
        Post post = event.getSource();

        CollectionUtils.emptyIfNull(post.getEnrolls()).stream().map(Enroll::getMember)
                .forEach(member ->
                        notificationRepository.save(
                                Notification.builder()
                                        .member(member)
                                        .checked(false)
                                        .domainEventType(event.getDomainEventType())
                                        .build()
                        )
                );
    }
}
