package com.flab.buywithme.repository;

import com.flab.buywithme.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findAllByMember_id(Long memberId, Pageable pageable);

    int countByMember_IdAndCheckedFalse(Long memberId);
}
