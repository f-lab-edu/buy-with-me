package com.flab.buywithme.service;

import com.flab.buywithme.domain.Member;
import com.flab.buywithme.domain.Post;
import com.flab.buywithme.domain.PostComment;
import com.flab.buywithme.dto.PostCommentDTO;
import com.flab.buywithme.dto.PostCommentResponseDto;
import com.flab.buywithme.error.CustomException;
import com.flab.buywithme.error.ErrorCode;
import com.flab.buywithme.event.DomainEvent;
import com.flab.buywithme.event.DomainEventType;
import com.flab.buywithme.repository.PostCommentRepository;
import com.flab.buywithme.service.common.CommonMemberService;
import com.flab.buywithme.service.common.CommonPostService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostCommentService {

    private final CommonMemberService commonMemberService;
    private final CommonPostService commonPostService;
    private final PostCommentRepository commentRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public Long saveComment(PostCommentDTO commentDTO, Long postId, Long memberId) {
        Member member = commonMemberService.getMember(memberId);

        Post post = commonPostService.getPost(postId);

        PostComment newComment = PostComment.builder()
                .post(post)
                .member(member)
                .content(commentDTO.getContent())
                .build();

        commentRepository.save(newComment);

        post.addComment(newComment);

        applicationEventPublisher.publishEvent(
                new DomainEvent<>(DomainEventType.CREATE_COMMENT, post));

        return newComment.getId();
    }

    public Long saveSubComment(Long commentId, PostCommentDTO commentDTO, Long postId,
            Long memberId) {
        PostComment parent = getComment(commentId);

        if (!parent.getPost().getId().equals(postId)) {
            throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
        }

        Member member = commonMemberService.getMember(memberId);

        Post post = commonPostService.getPost(postId);

        PostComment newComment = PostComment.builder()
                .post(post)
                .member(member)
                .content(commentDTO.getContent())
                .parent(parent)
                .build();

        commentRepository.save(newComment);

        parent.addChildren(newComment);

        applicationEventPublisher.publishEvent(
                new DomainEvent<>(DomainEventType.CREATE_SUB_COMMENT, parent));

        return newComment.getId();
    }

    @Transactional(readOnly = true)
    public Page<PostCommentResponseDto> getAllComment(Long postId, Pageable pageable) {
        return Optional.ofNullable(commentRepository.findAllByPost_Id(postId, pageable))
                .orElseGet(Page::empty)
                .map(PostCommentResponseDto::new);
    }

    @Transactional(readOnly = true)
    public PostComment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
    }

    public void updateComment(Long commentId, Long memberId, PostCommentDTO commentDTO) {
        PostComment comment = getComment(commentId);
        checkWhetherAuthor(comment, memberId);
        comment.update(commentDTO.getContent());
    }

    public void deleteComment(Long commentId, Long memberId) {
        PostComment comment = getComment(commentId);
        checkWhetherAuthor(comment, memberId);
        commentRepository.delete(comment);
    }

    public void checkWhetherAuthor(PostComment comment, Long memberId) {
        if (!comment.checkIsOwner(memberId)) {
            throw new CustomException(ErrorCode.IS_NOT_OWNER);
        }
    }
}
