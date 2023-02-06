package com.flab.buywithme.repository;

import com.flab.buywithme.domain.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByMember_id(Long memberId);

    int countByMember_IdAndCheckedFalse(Long memberId);
}
