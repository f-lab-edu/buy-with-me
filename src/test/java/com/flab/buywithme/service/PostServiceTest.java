package com.flab.buywithme.service;

import static com.flab.buywithme.TestFixture.fakeMember;
import static com.flab.buywithme.TestFixture.fakePageable;
import static com.flab.buywithme.TestFixture.fakePost;
import static com.flab.buywithme.TestFixture.fakePostDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.flab.buywithme.domain.Member;
import com.flab.buywithme.domain.Post;
import com.flab.buywithme.domain.enums.PostStatus;
import com.flab.buywithme.dto.PostDTO;
import com.flab.buywithme.error.CustomException;
import com.flab.buywithme.error.ErrorCode;
import com.flab.buywithme.event.DomainEvent;
import com.flab.buywithme.event.DomainEventType;
import com.flab.buywithme.repository.PostRepository;
import com.flab.buywithme.service.common.CommonMemberService;
import com.flab.buywithme.service.common.CommonPostService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;

@ExtendWith({MockitoExtension.class})
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private CommonPostService commonPostService;

    @Mock
    private CommonMemberService commonMemberService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    private Post post;
    private Long postId;
    private Long memberId;

    @BeforeEach
    public void setup() {
        postId = 1L;
        memberId = 1L;
        post = fakePost(postId);
    }

    @Test
    @DisplayName("게시글 저장 성공")
    public void savePost() {
        Member member = fakeMember(memberId);
        PostDTO postDTO = fakePostDTO();

        given(commonMemberService.getMember(anyLong()))
                .willReturn(member);
        given(postRepository.save(any(Post.class)))
                .willReturn(post);

        postService.savePost(postDTO, memberId);

        Post expected = Post.builder()
                .member(member)
                .address(member.getAddress())
                .title(postDTO.getTitle())
                .content(postDTO.getContent())
                .targetNo(postDTO.getTargetNo())
                .expiration(postDTO.getExpiration())
                .build();

        then(commonMemberService).should().getMember(memberId);
        then(postRepository).should().save(expected);
    }

    @Test
    @DisplayName("전체 게시글 가져오기 성공")
    public void getAllPostsSuccess() {
        Pageable pageable = fakePageable();

        postService.getAllPosts(pageable);

        then(postRepository).should().findAll(pageable);
    }

    @Test
    @DisplayName("키워드 기반 게시글 검색 성공")
    public void searchPostSuccess() {
        Pageable pageable = fakePageable();

        postService.searchPostWithKeyword("test", pageable);

        then(postRepository).should().findByTitleContainingOrContentContaining("test", pageable);
    }

    @Test
    @DisplayName("주소 기반 게시글 검색 성공")
    public void searchSameAddressPostSuccess() {
        Pageable pageable = fakePageable();
        Long addressId = 1L;

        postService.getPostsByAddress(addressId, pageable);

        then(postRepository).should().findByAddress_Id(addressId, pageable);
    }

    @Test
    @DisplayName("게시글 수정 성공")
    public void updatePostSuccess() {
        PostDTO updatePostDTO = PostDTO.builder()
                .title("수정 test 게시물")
                .content("test 목적으로 생성하였음")
                .targetNo(3)
                .expiration(LocalDateTime.of(2023, 4, 4, 23, 0, 0))
                .build();

        given(commonPostService.getPost(anyLong()))
                .willReturn(post);

        postService.updatePost(postId, memberId, updatePostDTO);

        then(commonPostService).should().getPost(postId);
        assertEquals(post.getTitle(), "수정 test 게시물");
    }

    @Test
    @DisplayName("게시글 상태 업데이트 성공")
    public void updatePostStatusSuccess() {
        DomainEvent<Post> expected = new DomainEvent<>(DomainEventType.UPDATE_POST, post);
        given(commonPostService.getPost(anyLong()))
                .willReturn(post);

        postService.updatePostStatus(postId, memberId, PostStatus.BUYING_COMPLETE);

        then(commonPostService).should().getPost(postId);
        assertEquals(PostStatus.BUYING_COMPLETE, post.getStatus());
        then(applicationEventPublisher).should().publishEvent(expected);
    }

    @Test
    @DisplayName("게시글 삭제 성공")
    public void deletePostSuccess() {
        DomainEvent<Post> expected = new DomainEvent<>(DomainEventType.DELETE_POST, post);
        given(commonPostService.getPost(anyLong()))
                .willReturn(post);

        postService.deletePost(postId, memberId);

        then(commonPostService).should().getPost(postId);
        then(postRepository).should().delete(fakePost(postId));
        then(applicationEventPublisher).should().publishEvent(expected);
    }

    @Test
    @DisplayName("작성자가 아니면 게시글 삭제 실패")
    public void deletePostFail() {
        given(commonPostService.getPost(anyLong()))
                .willReturn(post);

        CustomException ex = assertThrows(CustomException.class,
                () -> postService.deletePost(postId, 99L));

        then(commonPostService).should().getPost(postId);
        assertEquals(ex.getErrorCode(), ErrorCode.IS_NOT_OWNER);
    }
}
