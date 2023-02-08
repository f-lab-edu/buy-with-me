package com.flab.buywithme.service;

import static com.flab.buywithme.TestFixture.fakeNotification;
import static com.flab.buywithme.TestFixture.fakePageable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.flab.buywithme.domain.Notification;
import com.flab.buywithme.dto.NotificationResponseDTO;
import com.flab.buywithme.error.CustomException;
import com.flab.buywithme.error.ErrorCode;
import com.flab.buywithme.event.DomainEventType;
import com.flab.buywithme.repository.NotificationRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith({MockitoExtension.class})
class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    private Long notificationId;
    private Long memberId;
    private Notification notification;

    @BeforeEach
    public void setup() {
        notificationId = 1L;
        memberId = 1L;
        notification = fakeNotification(1L, DomainEventType.CREATE_COMMENT, false);
    }

    @Test
    @DisplayName("알림 리스트 가져오기 성공")
    void getAllNotifications() {
        List<Notification> notifications = Arrays.asList(
                fakeNotification(1L, DomainEventType.CREATE_COMMENT, true), //읽은 알림
                fakeNotification(2L, DomainEventType.GATHER_SUCCESS, false)); //안 읽은 알림
        Pageable pageable = fakePageable();
        Page<Notification> notificationPage = new PageImpl<>(notifications);
        NotificationResponseDTO expected = new NotificationResponseDTO(1, notificationPage);

        given(notificationRepository.findAllByMember_id(anyLong(), any(Pageable.class)))
                .willReturn(notificationPage);
        given(notificationRepository.countByMember_IdAndCheckedFalse(anyLong()))
                .willReturn(1);

        NotificationResponseDTO result = notificationService.getAllNotifications(
                memberId, pageable);

        then(notificationRepository).should().findAllByMember_id(memberId, pageable);
        then(notificationRepository).should().countByMember_IdAndCheckedFalse(memberId);
        assertEquals(expected, result);
    }


    @Test
    @DisplayName("특정 알림 가져오기 성공")
    void getNotificationSuccess() {
        given(notificationRepository.findById(anyLong()))
                .willReturn(Optional.ofNullable(notification));

        notificationService.getNotification(notificationId);

        then(notificationRepository).should().findById(notificationId);
    }

    @Test
    @DisplayName("특정 알림 가져오기 실패")
    void getNotificationFail() {
        given(notificationRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class,
                () -> notificationService.getNotification(notificationId));

        then(notificationRepository).should().findById(notificationId);
        assertEquals(ex.getErrorCode(), ErrorCode.NOTIFICATION_NOT_FOUND);
    }

    @Test
    @DisplayName("알림 읽기 성공")
    void readNotification() {
        given(notificationRepository.findById(anyLong()))
                .willReturn(Optional.ofNullable(notification));

        notificationService.readNotification(notificationId, memberId);

        then(notificationRepository).should().findById(notificationId);
        assertTrue(notification.isChecked());
    }

    @Test
    @DisplayName("알림 삭제 성공")
    void deleteNotification() {
        given(notificationRepository.findById(anyLong()))
                .willReturn(Optional.ofNullable(notification));

        notificationService.deleteNotification(notificationId, memberId);

        then(notificationRepository).should().findById(notificationId);
        then(notificationRepository).should().delete(notification);
    }

    @Test
    @DisplayName("작성자가 아니면 알림 삭제 실패")
    public void deleteNotificationFail() {
        given(notificationRepository.findById(anyLong()))
                .willReturn(Optional.ofNullable(notification));

        CustomException ex = assertThrows(CustomException.class,
                () -> notificationService.deleteNotification(notificationId, 99L));

        then(notificationRepository).should().findById(notificationId);
        assertEquals(ex.getErrorCode(), ErrorCode.IS_NOT_OWNER);
    }
}