package com.flab.buywithme.event.DomainEventHandlerImpl;

import com.flab.buywithme.domain.Enroll;
import com.flab.buywithme.domain.Member;
import com.flab.buywithme.domain.Notification;
import com.flab.buywithme.domain.Post;
import com.flab.buywithme.domain.enums.PostStatus;
import com.flab.buywithme.event.DomainEvent;
import com.flab.buywithme.event.DomainEventHandler;
import com.flab.buywithme.event.DomainEventType;
import com.flab.buywithme.repository.NotificationRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CreateNotificationWhenPostStatusUpdatedToBuyingComplete implements
        DomainEventHandler<Post> {

    private final NotificationRepository notificationRepository;

    @Override
    public boolean canHandle(DomainEvent<Post> event) {
        return event.getDomainEventType() == DomainEventType.UPDATE_POST
                && event.getSource().getStatus() == PostStatus.BUYING_COMPLETE;
    }

    @Override
    @Transactional
    public void handle(DomainEvent<Post> event) {
        Post post = event.getSource();

        List<Member> members = CollectionUtils.emptyIfNull(post.getEnrolls()).stream()
                .map(Enroll::getMember)
                .collect(Collectors.toList());

        members.add(post.getMember());

        for (Member member : members) {
            for (Member colleague : members) {
                if (member.equals(colleague)) {
                    continue;
                }
                notificationRepository.save(
                        Notification.builder()
                                .member(member)
                                .checked(false)
                                .formUrl("/colleagueEvaluateForm/" + post.getId() + "/"
                                        + colleague.getId())
                                .domainEventType(event.getDomainEventType())
                                .build()
                );
            }
        }
    }
}
