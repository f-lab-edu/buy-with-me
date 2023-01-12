package com.flab.buywithme.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.flab.buywithme.domain.Address;
import com.flab.buywithme.domain.Member;
import com.flab.buywithme.domain.Post;
import com.flab.buywithme.dto.PostDTO;
import com.flab.buywithme.error.CustomException;
import com.flab.buywithme.error.ErrorCode;
import com.flab.buywithme.repository.PostRepository;
import com.flab.buywithme.utils.HashingUtil;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    private Post post;
    private Long postId;
    private Long memberId;

    @BeforeEach
    public void setup() {
        post = fakePost(1L);
        postId = 1L;
        memberId = 1L;
    }

    @Test
    @DisplayName("게시글 저장 성공")
    public void savePost() {
        given(postRepository.save(any(Post.class)))
                .willReturn(post);

        postService.savePost(post);

        then(postRepository).should().save(fakePost(postId));
    }

    @Test
    @DisplayName("전체 게시글 가져오기 성공")
    public void getAllPost() {
        List<Post> posts = Arrays.asList(fakePost(1L), fakePost(2L));
        given(postRepository.findAll())
                .willReturn(posts);

        List<Post> expectList = Arrays.asList(fakePost(1L), fakePost(2L));
        assertTrue(postService.getAllPost().equals(expectList));
    }

    @Test
    @DisplayName("게시글 가져오기 성공")
    public void getPostSuccess() {
        given(postRepository.findById(postId))
                .willReturn(Optional.ofNullable(post));

        Post findPost = postService.getPost(postId);

        assertEquals(findPost, fakePost(postId));
    }

    @Test
    @DisplayName("게시글 가져오기 실패")
    public void getPostFail() {
        given(postRepository.findById(postId))
                .willReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class,
                () -> postService.getPost(postId));

        assertEquals(ex.getErrorCode(), ErrorCode.POST_NOT_FOUND);
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

        given(postRepository.findById(postId))
                .willReturn(Optional.ofNullable(post));

        postService.updatePost(postId, memberId, updatePostDTO);

        assertEquals(post.getTitle(), "수정 test 게시물");
    }

    @Test
    @DisplayName("게시글 삭제 성공")
    public void deletePostSuccess() {
        given(postRepository.findById(postId))
                .willReturn(Optional.ofNullable(post));

        postService.deletePost(postId, memberId);

        then(postRepository).should().delete(fakePost(postId));
    }

    @Test
    @DisplayName("작성자가 아니면 게시글 삭제 실패")
    public void deletePostFail() {
        given(postRepository.findById(postId))
                .willReturn(Optional.ofNullable(post));

        CustomException ex = assertThrows(CustomException.class,
                () -> postService.deletePost(postId, 99L));

        assertEquals(ex.getErrorCode(), ErrorCode.IS_NOT_OWNER);
    }

    private Member fakeMember(Long memberId) {
        Member member = new Member(new Address("성남시", "분당구", "판교동"), "kim", "010-1111-1111",
                "test", HashingUtil.encrypt("test1"));
        member.setId(memberId);
        member.getAddress().setId(1L);
        return member;
    }

    private Post fakePost(Long postId) {
        Member member = fakeMember(1L);
        return Post.builder()
                .id(postId)
                .member(member)
                .address(member.getAddress())
                .title("test 게시물")
                .content("test 목적으로 생성하였음")
                .targetNo(3)
                .expiration(LocalDateTime.of(2023, 4, 4, 23, 0, 0))
                .build();
    }
}