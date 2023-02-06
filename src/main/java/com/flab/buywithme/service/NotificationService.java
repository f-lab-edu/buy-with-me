package com.flab.buywithme.service;

import com.flab.buywithme.domain.Notification;
import com.flab.buywithme.dto.NotificationResponseDTO;
import com.flab.buywithme.error.CustomException;
import com.flab.buywithme.error.ErrorCode;
import com.flab.buywithme.repository.NotificationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public NotificationResponseDTO getAllNotifications(Long memberId) {
        List<Notification> notifications = notificationRepository.findAllByMember_id(memberId);
        int notChecked = notificationRepository.countByMember_IdAndCheckedFalse(memberId);
        return new NotificationResponseDTO(notChecked, notifications);
    }

    @Transactional(readOnly = true)
    public Notification getNotification(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));
    }

    public void readNotification(Long notificationId, Long memberId) {
        Notification notification = getNotification(notificationId);
        checkWhetherAuthor(notification, memberId);
        notification.read();
    }

    public void deleteNotification(Long notificationId, Long memberId) {
        Notification notification = getNotification(notificationId);
        checkWhetherAuthor(notification, memberId);
        notificationRepository.delete(notification);
    }

    private void checkWhetherAuthor(Notification notification, Long memberId) {
        if (!notification.checkIsOwner(memberId)) {
            throw new CustomException(ErrorCode.IS_NOT_OWNER);
        }
    }
}
