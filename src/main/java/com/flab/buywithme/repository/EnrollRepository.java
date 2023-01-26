package com.flab.buywithme.repository;

import com.flab.buywithme.domain.Enroll;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrollRepository extends JpaRepository<Enroll, Long> {

    Optional<Enroll> findByPost_IdAndMember_Id(Long postId, Long memberId);
}
