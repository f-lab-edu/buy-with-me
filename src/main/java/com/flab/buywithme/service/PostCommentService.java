package com.flab.buywithme.service;

import com.flab.buywithme.domain.Member;
import com.flab.buywithme.domain.Post;
import com.flab.buywithme.domain.PostComment;
import com.flab.buywithme.dto.PostCommentDTO;
import com.flab.buywithme.error.CustomException;
import com.flab.buywithme.error.ErrorCode;
import com.flab.buywithme.repository.PostCommentRepository;
import com.flab.buywithme.service.common.CommonMemberService;
import com.flab.buywithme.service.common.CommonPostService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostCommentService {

    private final CommonMemberService commonMemberService;
    private final CommonPostService commonPostService;
    private final PostCommentRepository commentRepository;

    public Long saveComment(PostCommentDTO commentDTO, Long postId, Long memberId) {
        Member member = commonMemberService.getMember(memberId);

        Post post = commonPostService.getPost(postId);

        PostComment newComment = PostComment.builder()
                .post(post)
                .member(member)
                .content(commentDTO.getContent())
                .build();

        return commentRepository.save(newComment).getId();
    }

    public Long saveSubComment(Long commentId, PostCommentDTO commentDTO, Long postId,
            Long memberId) {
        Member member = commonMemberService.getMember(memberId);

        Post post = commonPostService.getPost(postId);

        PostComment parent = getComment(commentId);

        PostComment newComment = PostComment.builder()
                .post(post)
                .member(member)
                .content(commentDTO.getContent())
                .parent(parent)
                .build();

        return commentRepository.save(newComment).getId();
    }

    @Transactional(readOnly = true)
    public List<PostComment> getAllComment(Long postId) {
        return commentRepository.findAllByPost_Id(postId);
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
