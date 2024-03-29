package com.flab.buywithme.domain;

import com.flab.buywithme.domain.enums.PostStatus;
import com.google.common.annotations.VisibleForTesting;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
    @Enumerated(EnumType.STRING)
    private PostStatus status = PostStatus.RUNNING;

    private LocalDateTime expiration;

    @CreatedDate
    private LocalDateTime createdAt;

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<PostComment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Enroll> enrolls = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<MemberEvaluation> evaluations = new ArrayList<>();

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
            this.status = PostStatus.GATHER_COMPLETE;
        }
    }

    public void updateStatus(PostStatus postStatus) {
        this.status = postStatus;
    }

    public void addEnroll(Enroll enroll) {
        enrolls.add(enroll);
    }

    public void addComment(PostComment comment) {
        comments.add(comment);
    }

    public void addEvaluation(MemberEvaluation evaluation) {
        evaluations.add(evaluation);
    }

    @VisibleForTesting
    public void setEnrolls(List<Enroll> enrollList) {
        this.enrolls = enrollList;
    }

    @VisibleForTesting
    public void setCurrentNo(int val) {
        this.currentNo = val;
    }
}
