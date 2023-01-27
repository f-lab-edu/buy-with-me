package com.flab.buywithme.domain;

import com.flab.buywithme.domain.enums.PostStatus;
import com.google.common.annotations.VisibleForTesting;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(of = "id")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;

    private String title;
    private String content;
    private int targetNo;

    @Builder.Default
    private int currentNo = 0;

    @Builder.Default
    private PostStatus status = PostStatus.RUNNING;

    private LocalDateTime expiration;

    @CreatedDate
    private LocalDateTime createdAt;

    public void update(String title, String content, int targetNo, LocalDateTime expiration) {
        this.title = title;
        this.content = content;
        this.targetNo = targetNo;
        this.expiration = expiration;
    }

    public boolean checkIsOwner(Long memberId) {
        return this.member.getId().equals(memberId);
    }

    public void increaseCurrentNo() {
        this.currentNo += 1;
        if (this.currentNo == this.targetNo) {
            this.status = PostStatus.COMPLETE;
        }
    }

    @VisibleForTesting
    public void setTitle(String title) {
        this.title = title;
    }

    @VisibleForTesting
    public void setContent(String content) {
        this.content = content;
    }

    @VisibleForTesting
    public void setCreatedAt(LocalDateTime createdTime) {
        this.createdAt = createdTime;
    }
}
