package com.flab.buywithme.controller;

import com.flab.buywithme.domain.Member;
import com.flab.buywithme.domain.Post;
import com.flab.buywithme.domain.PostComment;
import com.flab.buywithme.dto.PostCommentDTO;
import com.flab.buywithme.service.MemberService;
import com.flab.buywithme.service.PostCommentService;
import com.flab.buywithme.service.PostService;
import com.flab.buywithme.utils.SessionConst;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class PostCommentController {

    private final PostService postService;
    private final MemberService memberService;
    private final PostCommentService commentService;

    @PostMapping
    public void createComment(@Valid @RequestBody PostCommentDTO commentDTO,
            @PathVariable Long postId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId) {
        Member member = memberService.getMember(memberId);
        Post post = postService.getPost(postId);

        PostComment newComment = PostComment.builder()
                .post(post)
                .member(member)
                .content(commentDTO.getContent())
                .build();

        commentService.saveComment(newComment);
    }

    @GetMapping
    public List<PostComment> getAllComment(@PathVariable Long postId) {
        return commentService.getAllComment(postId);
    }

    @GetMapping("/{commentId}")
    public PostComment getComment(@PathVariable Long commentId) {
        return commentService.getComment(commentId);
    }

    @PutMapping("/{commentId}")
    public void updateComment(@PathVariable Long commentId,
            @Valid @RequestBody PostCommentDTO commentDTO,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId) {
        commentService.updateComment(commentId, memberId, commentDTO);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long commentId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId) {
        commentService.deleteComment(commentId, memberId);
    }
}
