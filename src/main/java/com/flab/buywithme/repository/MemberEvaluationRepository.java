package com.flab.buywithme.repository;

import com.flab.buywithme.domain.MemberEvaluation;
import com.google.common.annotations.VisibleForTesting;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberEvaluationRepository extends JpaRepository<MemberEvaluation, Long> {

    List<MemberEvaluation> findAllByColleagueId(Long memberId);

    @VisibleForTesting
    List<MemberEvaluation> findAllByPost_Id(Long postId);
}
