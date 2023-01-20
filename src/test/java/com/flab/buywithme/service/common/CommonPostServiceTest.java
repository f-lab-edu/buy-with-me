package com.flab.buywithme.service.common;

import static com.flab.buywithme.TestFixture.fakePost;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.flab.buywithme.domain.Post;
import com.flab.buywithme.error.CustomException;
import com.flab.buywithme.error.ErrorCode;
import com.flab.buywithme.repository.PostRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
class CommonPostServiceTest {

    @InjectMocks
    private CommonPostService commonPostService;

    @Mock
    private PostRepository postRepository;

    private Long postId;
    private Post post;

    @BeforeEach
    public void setup() {
        postId = 1L;
        post = fakePost(postId);
    }

    @Test
    @DisplayName("게시글 가져오기 성공")
    public void getPostSuccess() {
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.ofNullable(post));

        Post findPost = commonPostService.getPost(postId);

        assertEquals(findPost, fakePost(postId));
    }

    @Test
    @DisplayName("게시글 가져오기 실패")
    public void getPostFail() {
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class,
                () -> commonPostService.getPost(postId));

        assertEquals(ex.getErrorCode(), ErrorCode.POST_NOT_FOUND);
    }
}
