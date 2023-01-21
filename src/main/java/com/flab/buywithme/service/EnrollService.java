package com.flab.buywithme.service;

import com.flab.buywithme.domain.Enroll;
import com.flab.buywithme.domain.Member;
import com.flab.buywithme.domain.Post;
import com.flab.buywithme.error.CustomException;
import com.flab.buywithme.error.ErrorCode;
import com.flab.buywithme.repository.EnrollRepository;
import com.flab.buywithme.service.common.CommonMemberService;
import com.flab.buywithme.service.common.CommonPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EnrollService {

    private final EnrollRepository enrollRepository;
    private final CommonPostService commonPostService;
    private final CommonMemberService commonMemberService;

    public void joinBuying(Long postId, Long memberId) {
        enrollRepository.findByPost_idAndMember_Id(postId, memberId)
                .ifPresent(e -> {
                    throw new CustomException(ErrorCode.ENROLL_ALREADY_DONE);
                });

        Post post = commonPostService.getPost(postId);
        Member member = commonMemberService.getMember(memberId);
        Enroll enroll = Enroll.builder()
                .member(member)
                .post(post)
                .build();

        commonPostService.increaseJoinCount(post);
        enrollRepository.save(enroll);
    }

    public void cancelJoining(Long postId, Long memberId) {
        enrollRepository.delete(enrollRepository.findByPost_idAndMember_Id(postId,
                memberId).orElseThrow(() -> new CustomException(ErrorCode.ENROLL_NOT_FOUND)));
    }
}
