package com.flab.buywithme.controller;

import com.flab.buywithme.domain.Post;
import com.flab.buywithme.domain.enums.PostStatus;
import com.flab.buywithme.dto.PostDTO;
import com.flab.buywithme.service.PostService;
import com.flab.buywithme.service.common.CommonPostService;
import com.flab.buywithme.utils.SessionConst;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CommonPostService commonPostService;

    @PostMapping
    public void createPost(@Valid @RequestBody PostDTO postDTO,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId) {
        postService.savePost(postDTO, memberId);
    }

    @GetMapping
    public Page<Post> getAllPosts(
            @PageableDefault(sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        return postService.getAllPosts(pageable);
    }

    @GetMapping(params = "keyword")
    public Page<Post> getPostsWithKeyword(
            @RequestParam String keyword,
            @PageableDefault(sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        return postService.searchPostWithKeyword(keyword, pageable);
    }

    @GetMapping("/addresses/{addressId}")
    public Page<Post> getSameAddressPosts(
            @PathVariable Long addressId,
            @PageableDefault(sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        return postService.getPostsByAddress(addressId, pageable);
    }

    @GetMapping("/{postId}")
    public Post getPost(@PathVariable Long postId) {
        return commonPostService.getPost(postId);
    }

    @PatchMapping("/{postId}")
    public void updatePostStatus(@PathVariable Long postId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId,
            @RequestParam("postStatus") PostStatus postStatus) {
        postService.updatePostStatus(postId, memberId, postStatus);
    }

    @PutMapping("/{postId}")
    public void updatePost(@PathVariable Long postId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId,
            @Valid @RequestBody PostDTO postDTO) {
        postService.updatePost(postId, memberId, postDTO);
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable Long postId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId) {
        postService.deletePost(postId, memberId);
    }
}
