package com.flab.buywithme.service;

import com.flab.buywithme.domain.Member;
import com.flab.buywithme.domain.Post;
import com.flab.buywithme.domain.enums.PostStatus;
import com.flab.buywithme.dto.PostDTO;
import com.flab.buywithme.dto.PostResponseDto;
import com.flab.buywithme.error.CustomException;
import com.flab.buywithme.error.ErrorCode;
import com.flab.buywithme.event.DomainEvent;
import com.flab.buywithme.event.DomainEventType;
import com.flab.buywithme.repository.PostRepository;
import com.flab.buywithme.service.common.CommonMemberService;
import com.flab.buywithme.service.common.CommonPostService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final CommonPostService commonPostService;
    private final CommonMemberService commonMemberService;
    private final PostRepository postRepository;
    private final ApplicationEventPublisher eventPublisher;

    @CacheEvict(cacheNames = "postPage", allEntries = true)
    public Post savePost(PostDTO postDTO, Long memberId) {
        Member findMember = commonMemberService.getMember(memberId);

        Post newPost = Post.builder()
                .member(findMember)
                .address(findMember.getAddress())
                .title(postDTO.getTitle())
                .content(postDTO.getContent())
                .targetNo(postDTO.getTargetNo())
                .expiration(postDTO.getExpiration())
                .build();

        return postRepository.save(newPost);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "postPage", key = "#pageable.pageNumber")
    public Page<PostResponseDto> getAllPosts(Pageable pageable) {
        return Optional.ofNullable(postRepository.findAllUsingFetchJoin(pageable))
                .orElseGet(Page::empty)
                .map(PostResponseDto::new);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "postPage", key = "#keyword + ':' + #pageable.pageNumber")
    public Page<PostResponseDto> searchPostWithKeyword(String keyword, Pageable pageable) {
        return Optional.ofNullable(
                        postRepository.findByTitleContainingOrContentContaining(keyword, pageable))
                .orElseGet(Page::empty)
                .map(PostResponseDto::new);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "postPage", key = "#addressId + ':' + #pageable.pageNumber")
    public Page<PostResponseDto> getPostsByAddress(Long addressId, Pageable pageable) {
        return Optional.ofNullable(postRepository.findByAddress_Id(addressId, pageable))
                .orElseGet(Page::empty)
                .map(PostResponseDto::new);
    }

    @CacheEvict(cacheNames = "postPage", allEntries = true)
    public void updatePostStatus(Long postId, Long memberId, PostStatus postStatus) {
        Post post = commonPostService.getPost(postId);
        checkWhetherAuthor(post, memberId);
        post.updateStatus(postStatus);

        eventPublisher.publishEvent(
                new DomainEvent<>(DomainEventType.UPDATE_POST, post));
    }

    @CacheEvict(cacheNames = "postPage", allEntries = true)
    public void updatePost(Long postId, Long memberId, PostDTO postDTO) {
        Post post = commonPostService.getPost(postId);
        checkWhetherAuthor(post, memberId);
        post.update(postDTO.getTitle(), postDTO.getContent(), postDTO.getTargetNo(),
                postDTO.getExpiration());
    }

    @CacheEvict(cacheNames = "postPage", allEntries = true)
    public void deletePost(Long postId, Long memberId) {
        Post post = commonPostService.getPost(postId);
        checkWhetherAuthor(post, memberId);
        postRepository.delete(post);

        eventPublisher.publishEvent(
                new DomainEvent<>(DomainEventType.DELETE_POST, post));
    }

    public void checkWhetherAuthor(Post post, Long memberId) {
        if (!post.checkIsOwner(memberId)) {
            throw new CustomException(ErrorCode.IS_NOT_OWNER);
        }
    }
}
