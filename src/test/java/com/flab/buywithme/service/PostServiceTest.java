package com.flab.buywithme.service;

import static com.flab.buywithme.TestFixture.fakeMember;
import static com.flab.buywithme.TestFixture.fakePost;
import static com.flab.buywithme.TestFixture.fakePostDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.flab.buywithme.domain.Member;
import com.flab.buywithme.domain.Post;
import com.flab.buywithme.dto.PostDTO;
import com.flab.buywithme.error.CustomException;
import com.flab.buywithme.error.ErrorCode;
import com.flab.buywithme.repository.PostRepository;
import com.flab.buywithme.service.common.CommonMemberService;
import com.flab.buywithme.service.common.CommonPostService;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

        then(postRepository).should().save(expected);
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

        assertEquals(post.getTitle(), "수정 test 게시물");
    }

    @Test
    @DisplayName("게시글 삭제 성공")
    public void deletePostSuccess() {
        given(commonPostService.getPost(anyLong()))
                .willReturn(post);

        postService.deletePost(postId, memberId);

        then(postRepository).should().delete(fakePost(postId));
    }

    @Test
    @DisplayName("작성자가 아니면 게시글 삭제 실패")
    public void deletePostFail() {
        given(commonPostService.getPost(anyLong()))
                .willReturn(post);

        CustomException ex = assertThrows(CustomException.class,
                () -> postService.deletePost(postId, 99L));

        assertEquals(ex.getErrorCode(), ErrorCode.IS_NOT_OWNER);
    }
}
