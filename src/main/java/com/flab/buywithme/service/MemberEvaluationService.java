package com.flab.buywithme.service;

import com.flab.buywithme.domain.Member;
import com.flab.buywithme.domain.MemberEvaluation;
import com.flab.buywithme.domain.Post;
import com.flab.buywithme.dto.MemberEvaluationDTO;
import com.flab.buywithme.error.CustomException;
import com.flab.buywithme.error.ErrorCode;
import com.flab.buywithme.repository.MemberEvaluationRepository;
import com.flab.buywithme.service.common.CommonMemberService;
import com.flab.buywithme.service.common.CommonPostService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberEvaluationService {

    private final CommonMemberService commonMemberService;
    private final CommonPostService commonPostService;
    private final MemberEvaluationRepository memberEvaluationRepository;

    public Long saveEvaluation(MemberEvaluationDTO evaluationDTO, Long postId, Long memberId,
            Long colleagueId) {
        Member member = commonMemberService.getMember(memberId);

        Member colleague = commonMemberService.getMember(colleagueId);

        Post post = commonPostService.getPost(postId);

        CollectionUtils.emptyIfNull(post.getEnrolls()).stream()
                .filter(e -> e.getMember().equals(member)).findAny()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_ENROLLED_MEMBER));

        CollectionUtils.emptyIfNull(post.getEnrolls()).stream()
                .filter(e -> e.getMember().equals(colleague)).findAny()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_ENROLLED_MEMBER));

        MemberEvaluation newEvaluation = MemberEvaluation.builder()
                .post(post)
                .member(member)
                .colleague(colleague)
                .content(evaluationDTO.getContent())
                .build();

        memberEvaluationRepository.save(newEvaluation);

        post.addEvaluation(newEvaluation);

        return newEvaluation.getId();
    }

    @Transactional(readOnly = true)
    public List<MemberEvaluation> getAllEvaluations(Long memberId) {
        return memberEvaluationRepository.findAllByColleagueId(memberId);
    }

    @Transactional(readOnly = true)
    public MemberEvaluation getEvaluation(Long evaluationId) {
        return memberEvaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new CustomException(ErrorCode.EVALUATION_NOT_FOUND));
    }
}
