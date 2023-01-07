package com.flab.buywithme.controller;

import com.flab.buywithme.domain.Member;
import com.flab.buywithme.domain.Post;
import com.flab.buywithme.dto.PostDTO;
import com.flab.buywithme.service.MemberService;
import com.flab.buywithme.service.PostService;
import com.flab.buywithme.utils.SessionConst;
import java.util.List;
import java.util.Optional;
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
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final MemberService memberService;

    @PostMapping
    public void createPost(@Valid @RequestBody PostDTO postDTO,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId) {
        Member findMember = memberService.findById(memberId).get();

        Post newPost = Post.builder()
                .member(findMember)
                .address(findMember.getAddress())
                .title(postDTO.getTitle())
                .content(postDTO.getContent())
                .targetNo(postDTO.getTargetNo())
                .expiration(postDTO.getExpiration())
                .build();

        postService.savePost(newPost);
    }

    @GetMapping
    public List<Post> getAllPost() {
        return postService.getAllPost();
    }

    @GetMapping("/{postId}")
    public Optional<Post> getPost(@PathVariable Long postId) {
        return postService.getPost(postId);
    }

    @PutMapping("/{postId}")
    public void updatePost(@PathVariable Long postId, @Valid @RequestBody PostDTO postDTO) {
        postService.updatePost(postId, postDTO);
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
    }
}
