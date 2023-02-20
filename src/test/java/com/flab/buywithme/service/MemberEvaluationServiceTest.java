package com.flab.buywithme.service;

import static com.flab.buywithme.TestFixture.fakeEnroll;
import static com.flab.buywithme.TestFixture.fakeMember;
import static com.flab.buywithme.TestFixture.fakeMemberEvaluation;
import static com.flab.buywithme.TestFixture.fakeMemberEvaluationDTO;
import static com.flab.buywithme.TestFixture.fakePost;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.flab.buywithme.domain.Enroll;
import com.flab.buywithme.domain.Member;
import com.flab.buywithme.domain.MemberEvaluation;
import com.flab.buywithme.domain.Post;
import com.flab.buywithme.dto.MemberEvaluationDTO;
import com.flab.buywithme.error.CustomException;
import com.flab.buywithme.error.ErrorCode;
import com.flab.buywithme.repository.MemberEvaluationRepository;
import com.flab.buywithme.service.common.CommonMemberService;
import com.flab.buywithme.service.common.CommonPostService;
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
class MemberEvaluationServiceTest {

    @InjectMocks
    private MemberEvaluationService memberEvaluationService;

    @Mock
    private MemberEvaluationRepository memberEvaluationRepository;

    @Mock
    private CommonMemberService commonMemberService;

    @Mock
    private CommonPostService commonPostService;

    private Long postId;
    private Long memberId;
    private Long colleagueId;
    private Long evaluationId;
    private MemberEvaluation memberEvaluation;

    @BeforeEach
    public void setup() {
        postId = 1L;
        memberId = 1L;
        colleagueId = 2L;
        evaluationId = 1L;
        memberEvaluation = fakeMemberEvaluation(evaluationId);
    }

    @Test
    @DisplayName("매너 평가 성공")
    public void saveEvaluationSuccess() {
        Member currentMember = fakeMember(memberId);
        Member colleague = fakeMember(colleagueId);
        Post currentPost = fakePost(postId);
        List<Enroll> fakeEnrollList = Arrays.asList(fakeEnroll(1L), fakeEnroll(2L));
        fakeEnrollList.get(1).getMember().setId(colleagueId);
        currentPost.setEnrolls(fakeEnrollList);
        MemberEvaluationDTO memberEvaluationDTO = fakeMemberEvaluationDTO();

        given(commonMemberService.getMember(memberId))
                .willReturn(currentMember);
        given(commonMemberService.getMember(colleagueId))
                .willReturn(colleague);
        given(commonPostService.getPost(anyLong()))
                .willReturn(currentPost);
        given(memberEvaluationRepository.save(any(MemberEvaluation.class)))
                .willReturn(memberEvaluation);

        memberEvaluationService.saveEvaluation(memberEvaluationDTO, postId, memberId, colleagueId);

        MemberEvaluation expected = MemberEvaluation.builder()
                .post(currentPost)
                .member(currentMember)
                .colleague(colleague)
                .content(memberEvaluationDTO.getContent())
                .build();

        then(commonMemberService).should().getMember(memberId);
        then(commonMemberService).should().getMember(colleagueId);
        then(commonPostService).should().getPost(postId);
        then(memberEvaluationRepository).should().save(expected);
    }

    @Test
    @DisplayName("구매에 참여하지 않은 멤버에 대한 평가는 저장 실패")
    public void saveEvaluationFail() {
        Member currentMember = fakeMember(memberId);
        Member colleague = fakeMember(colleagueId);
        Post currentPost = fakePost(postId);
        currentPost.setEnrolls(Arrays.asList(fakeEnroll(1L)));
        MemberEvaluationDTO memberEvaluationDTO = fakeMemberEvaluationDTO();

        given(commonMemberService.getMember(memberId))
                .willReturn(currentMember);
        given(commonMemberService.getMember(colleagueId))
                .willReturn(colleague);
        given(commonPostService.getPost(anyLong()))
                .willReturn(currentPost);

        CustomException ex = assertThrows(CustomException.class,
                () -> memberEvaluationService.saveEvaluation(memberEvaluationDTO, postId,
                        memberId, colleagueId));

        then(commonMemberService).should().getMember(memberId);
        then(commonMemberService).should().getMember(colleagueId);
        then(commonPostService).should().getPost(postId);
        assertEquals(ex.getErrorCode(), ErrorCode.NOT_ENROLLED_MEMBER);
        verify(memberEvaluationRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("멤버별 받은 매너 평가 내역 가져오기 성공")
    public void getAllEvaluations() {
        memberEvaluationService.getAllEvaluations(memberId);
        then(memberEvaluationRepository).should().findAllByColleagueId(memberId);
    }

    @Test
    @DisplayName("특정 매너 평가 가져오기 성공")
    public void getEvaluationSuccess() {
        given(memberEvaluationRepository.findById(anyLong()))
                .willReturn(Optional.ofNullable(memberEvaluation));

        memberEvaluationService.getEvaluation(evaluationId);

        then(memberEvaluationRepository).should().findById(evaluationId);
    }

    @Test
    @DisplayName("특정 매너 평가 가져오기 실패")
    public void getEvaluationFail() {
        given(memberEvaluationRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class,
                () -> memberEvaluationService.getEvaluation(evaluationId));

        then(memberEvaluationRepository).should().findById(evaluationId);
        assertEquals(ex.getErrorCode(), ErrorCode.EVALUATION_NOT_FOUND);
    }
}
