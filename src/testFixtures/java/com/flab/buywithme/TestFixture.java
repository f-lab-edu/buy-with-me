package com.flab.buywithme;

import com.flab.buywithme.domain.Address;
import com.flab.buywithme.domain.Enroll;
import com.flab.buywithme.domain.Member;
import com.flab.buywithme.domain.MemberEvaluation;
import com.flab.buywithme.domain.Notification;
import com.flab.buywithme.domain.Post;
import com.flab.buywithme.domain.PostComment;
import com.flab.buywithme.dto.MemberEvaluationDTO;
import com.flab.buywithme.dto.PostCommentDTO;
import com.flab.buywithme.dto.PostDTO;
import com.flab.buywithme.event.DomainEventType;
import com.flab.buywithme.utils.HashingUtil;
import java.time.LocalDateTime;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

public class TestFixture {

    private static final Long defaultMemberID = 1L;
    private static final Long defaultPostID = 1L;
    private static final Long defaultCommentID = 1L;

    public static Member fakeMember(Long memberId) {
        Member member = new Member(new Address("성남시", "분당구", "판교동"), "kim", "010-1111-1111",
                "test", HashingUtil.encrypt("test1"));
        member.setId(memberId);
        member.getAddress().setId(1L);
        return member;
    }

    public static Address fakeAddress(Long addressId) {
        Address address = new Address("성남시", "분당구", "판교동");
        address.setId(addressId);
        return address;
    }

    public static Post fakePost(Long postId) {
        Member member = fakeMember(defaultMemberID);
        return Post.builder()
                .id(postId)
                .member(member)
                .address(member.getAddress())
                .title("test 게시물")
                .content("test 목적으로 생성하였음")
                .targetNo(2)
                .expiration(LocalDateTime.of(2023, 4, 4, 23, 0, 0))
                .build();
    }

    public static PostDTO fakePostDTO() {
        return PostDTO.builder()
                .title("test 게시물")
                .content("test 목적으로 생성하였음")
                .targetNo(3)
                .expiration(LocalDateTime.of(2023, 4, 4, 23, 0, 0))
                .build();
    }

    public static PostComment fakeComment(Long commentId) {
        Post post = fakePost(defaultPostID);
        Member member = fakeMember(defaultMemberID);
        return PostComment.builder()
                .id(commentId)
                .post(post)
                .member(member)
                .content("test comment")
                .build();
    }

    public static PostComment fakeSubComment(Long subCommentId) {
        Post post = fakePost(defaultPostID);
        Member member = fakeMember(defaultMemberID);
        PostComment comment = fakeComment(defaultCommentID);
        return PostComment.builder()
                .id(subCommentId)
                .post(post)
                .member(member)
                .content("test comment")
                .parent(comment)
                .build();
    }

    public static PostCommentDTO fakeCommentDTO() {
        return new PostCommentDTO("test comment");
    }

    public static Enroll fakeEnroll(Long enrollId) {
        Post post = fakePost(defaultPostID);
        Member member = fakeMember(defaultMemberID);
        return Enroll.builder()
                .id(enrollId)
                .member(member)
                .post(post)
                .build();
    }

    public static Pageable fakePageable() {
        return PageRequest.of(0, 2, Sort.by(Order.desc("createdAt")));
    }

    public static Notification fakeNotification(Long notificationId, DomainEventType type,
            Boolean checked) {
        Member member = fakeMember(defaultMemberID);
        return Notification.builder()
                .id(notificationId)
                .member(member)
                .domainEventType(type)
                .checked(checked)
                .build();
    }

    public static MemberEvaluation fakeMemberEvaluation(Long evaluationId) {
        Post post = fakePost(defaultPostID);
        Member member = fakeMember(defaultMemberID);
        Member colleague = fakeMember(2L);

        return MemberEvaluation.builder()
                .id(evaluationId)
                .post(post)
                .member(member)
                .colleague(colleague)
                .content("test eval")
                .build();
    }

    public static MemberEvaluationDTO fakeMemberEvaluationDTO() {
        return new MemberEvaluationDTO("test eval");
    }
}
