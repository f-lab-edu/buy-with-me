package com.flab.buywithme.dto;

import com.flab.buywithme.domain.Post;
import com.flab.buywithme.domain.enums.PostStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class PostResponseDto {

    private Long id;
    private Long memberId;
    private Long addressId;
    private String title;
    private String content;
    private int targetNo;
    private int currentNo;
    private PostStatus status;
    private LocalDateTime expiration;
    private LocalDateTime createdAt;
    private List<PostCommentResponseDto> comments;

    /* Entity -> Dto*/
    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.memberId = post.getMember().getId();
        this.addressId = post.getAddress().getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.targetNo = post.getTargetNo();
        this.currentNo = post.getCurrentNo();
        this.status = post.getStatus();
        this.expiration = post.getExpiration();
        this.createdAt = post.getCreatedAt();
        this.comments = post.getComments().stream().map(PostCommentResponseDto::new).collect(
                Collectors.toList());
    }
}
