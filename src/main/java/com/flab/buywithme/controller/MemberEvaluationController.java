package com.flab.buywithme.controller;

import com.flab.buywithme.domain.MemberEvaluation;
import com.flab.buywithme.dto.MemberEvaluationDTO;
import com.flab.buywithme.service.MemberEvaluationService;
import com.flab.buywithme.utils.SessionConst;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequestMapping("/evaluations")
@RequiredArgsConstructor
public class MemberEvaluationController {

    private final MemberEvaluationService memberEvaluationService;

    @PostMapping("/{postId}/{memberId}")
    public void createEvaluation(@Valid @RequestBody MemberEvaluationDTO evaluationDTO,
            @PathVariable Long postId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId,
            @PathVariable("memberId") Long colleagueId) {
        memberEvaluationService.saveEvaluation(evaluationDTO, postId, memberId, colleagueId);
    }

    @GetMapping
    public List<MemberEvaluation> getAllEvaluations(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId) {
        return memberEvaluationService.getAllEvaluations(memberId);
    }

    @GetMapping("/{evaluationId}")
    public MemberEvaluation getEvaluation(@PathVariable Long evaluationId) {
        return memberEvaluationService.getEvaluation(evaluationId);
    }
}
