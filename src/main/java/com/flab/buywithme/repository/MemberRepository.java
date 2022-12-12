package com.flab.buywithme.repository;

import com.flab.buywithme.domain.Member;
import java.util.List;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findByLoginId(String loginId) {
        return em.createQuery("select u from Member u where u.loginId = :loginId", Member.class)
                .setParameter("loginId", loginId)
                .getResultList();
    }

}
