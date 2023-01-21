package com.flab.buywithme.service.common;

import static com.flab.buywithme.TestFixture.fakePost;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.flab.buywithme.domain.Post;
import com.flab.buywithme.error.CustomException;
import com.flab.buywithme.error.ErrorCode;
import com.flab.buywithme.repository.PostRepository;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

        then(postRepository).should().findById(postId);
        assertEquals(findPost, fakePost(postId));
    }

    @Test
    @DisplayName("게시글 가져오기 실패")
    public void getPostFail() {
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class,
                () -> commonPostService.getPost(postId));

        then(postRepository).should().findById(postId);
        assertEquals(ex.getErrorCode(), ErrorCode.POST_NOT_FOUND);
    }

    @Test
    @DisplayName("구매 참여 요청 시 currentNo 값 1 증가")
    public void increaseJoinCountSuccess() {
        int beforeCurrentNo = post.getCurrentNo();

        commonPostService.increaseJoinCount(post);

        assertEquals(beforeCurrentNo + 1, post.getCurrentNo());
    }

    @Test
    @DisplayName("인원 모집 완료된 경우 currentNo 값 변화 없이 예외 발생")
    public void increaseJoinCountSuccessFail() {
        for (int i = 0; i < post.getTargetNo(); i++) {
            commonPostService.increaseJoinCount(post);
        }

        CustomException ex = assertThrows(CustomException.class,
                () -> commonPostService.increaseJoinCount(post));

        assertEquals(ex.getErrorCode(), ErrorCode.GATHERING_FINISHED);
    }

    @Disabled
    @Test
    @DisplayName("동시 구매 참여 요청시 currentNo 값이 요청 수만큼 늘어남")
    public void SimultaneousRequestProcessingSuccess() throws InterruptedException {
        int numOfExecute = post.getTargetNo();
        ExecutorService executorService = Executors.newFixedThreadPool(numOfExecute);
        CountDownLatch countDownLatch = new CountDownLatch(numOfExecute);

        for (int i = 0; i < numOfExecute; i++) {
            executorService.execute(() -> {
                commonPostService.increaseJoinCount(post);
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        assertEquals(post.getTargetNo(), post.getCurrentNo());
    }
}
