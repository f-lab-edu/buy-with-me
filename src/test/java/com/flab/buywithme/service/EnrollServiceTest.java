package com.flab.buywithme.service;

import static com.flab.buywithme.TestFixture.fakeEnroll;
import static com.flab.buywithme.TestFixture.fakeMember;
import static com.flab.buywithme.TestFixture.fakePost;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.flab.buywithme.domain.Enroll;
import com.flab.buywithme.domain.Member;
import com.flab.buywithme.domain.Post;
import com.flab.buywithme.error.CustomException;
import com.flab.buywithme.error.ErrorCode;
import com.flab.buywithme.repository.EnrollRepository;
import com.flab.buywithme.service.common.CommonMemberService;
import com.flab.buywithme.service.common.CommonPostService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
class EnrollServiceTest {

    @InjectMocks
    private EnrollService enrollService;

    @Mock
    private CommonPostService commonPostService;

    @Mock
    private CommonMemberService commonMemberService;

    @Mock
    private EnrollRepository enrollRepository;

    private Long postId;
    private Long memberId;
    private Long enrollId;
    private Enroll enroll;

    @BeforeEach
    void setUp() {
        postId = 1L;
        memberId = 1L;
        enrollId = 1L;
        enroll = fakeEnroll(enrollId);
    }

    @Test
    @DisplayName("구매 참여 성공")
    void joinBuyingSuccess() {
        Member member = fakeMember(memberId);
        Post post = fakePost(postId);

        given(enrollRepository.findByPost_idAndMember_Id(anyLong(), anyLong()))
                .willReturn(Optional.empty());
        given(commonMemberService.getMember(anyLong()))
                .willReturn(member);
        given(commonPostService.getPost(anyLong()))
                .willReturn(post);

        enrollService.joinBuying(postId, memberId);

        Enroll expected = Enroll.builder()
                .member(member)
                .post(post)
                .build();

        then(commonPostService).should().increaseJoinCount(post);
        then(enrollRepository).should().save(expected);
    }

    @Test
    @DisplayName("이미 참여한 구매에 대해 중복 참여 실패")
    void joinBuyingFail() {
        given(enrollRepository.findByPost_idAndMember_Id(anyLong(), anyLong()))
                .willReturn(Optional.ofNullable(enroll));

        CustomException ex = assertThrows(CustomException.class,
                () -> enrollService.joinBuying(postId, memberId));

        assertEquals(ErrorCode.ENROLL_ALREADY_DONE, ex.getErrorCode());
    }

    @Test
    @DisplayName("구매 참여 취소 성공")
    void cancelJoiningSuccess() {
        given(enrollRepository.findByPost_idAndMember_Id(anyLong(), anyLong()))
                .willReturn(Optional.ofNullable(enroll));

        enrollService.cancelJoining(postId, memberId);

        then(enrollRepository).should().delete(enroll);
    }

    @Test
    @DisplayName("구매 참여 이력이 없을 경우 취소 실패")
    void cancelJoiningFail() {
        given(enrollRepository.findByPost_idAndMember_Id(anyLong(), anyLong()))
                .willReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class,
                () -> enrollService.cancelJoining(postId, memberId));

        assertEquals(ErrorCode.ENROLL_NOT_FOUND, ex.getErrorCode());
    }
}
