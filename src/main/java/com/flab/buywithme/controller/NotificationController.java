package com.flab.buywithme.controller;

import com.flab.buywithme.dto.NotificationResponseDTO;
import com.flab.buywithme.service.NotificationService;
import com.flab.buywithme.utils.SessionConst;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public NotificationResponseDTO getAllNotifications(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId,
            @PageableDefault(sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        return notificationService.getAllNotifications(memberId, pageable);
    }

    @PutMapping("/{notificationId}")
    public void readNotification(@PathVariable Long notificationId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId) {
        notificationService.readNotification(notificationId, memberId);
    }

    @DeleteMapping("/{notificationId}")
    public void deleteNotification(@PathVariable Long notificationId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId) {
        notificationService.deleteNotification(notificationId, memberId);
    }
}
