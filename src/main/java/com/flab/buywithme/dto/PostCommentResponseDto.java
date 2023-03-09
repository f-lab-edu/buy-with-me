package com.flab.buywithme.dto;

import com.flab.buywithme.domain.PostComment;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class PostCommentResponseDto {

    private Long id;
    private Long postId;
    private Long memberId;
    private String content;
    private List<PostCommentResponseDto> children;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /* Entity -> Dto*/
    public PostCommentResponseDto(PostComment comment) {
        this.id = comment.getId();
        this.postId = comment.getPost().getId();
        this.memberId = comment.getMember().getId();
        this.content = comment.getContent();
        this.children = comment.getChildren().stream().map(PostCommentResponseDto::new).collect(
                Collectors.toList());
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
    }
}
