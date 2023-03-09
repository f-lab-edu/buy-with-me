package com.flab.buywithme.controller;

import com.flab.buywithme.domain.PostComment;
import com.flab.buywithme.dto.PostCommentDTO;
import com.flab.buywithme.dto.PostCommentResponseDto;
import com.flab.buywithme.service.PostCommentService;
import com.flab.buywithme.utils.SessionConst;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
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

    private final PostCommentService commentService;

    @PostMapping
    public void createComment(@Valid @RequestBody PostCommentDTO commentDTO,
            @PathVariable Long postId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId) {
        commentService.saveComment(commentDTO, postId, memberId);
    }

    @PostMapping("/{commentId}")
    public void createSubComment(@PathVariable Long commentId,
            @Valid @RequestBody PostCommentDTO commentDTO,
            @PathVariable Long postId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId) {
        commentService.saveSubComment(commentId, commentDTO, postId, memberId);
    }

    @GetMapping
    public Page<PostCommentResponseDto> getAllComment(@PathVariable Long postId,
            @PageableDefault(sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        return commentService.getAllComment(postId, pageable);
    }

    @GetMapping("/{commentId}")
    public PostComment getComment(@PathVariable Long commentId) {
        return commentService.getComment(commentId); //PostCommentResponseDto를 반환하도록 수정 예정
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
