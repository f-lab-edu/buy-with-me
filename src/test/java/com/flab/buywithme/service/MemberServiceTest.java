package com.flab.buywithme.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.flab.buywithme.domain.Address;
import com.flab.buywithme.domain.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Test
    public void 회원가입() {
        //given
        Member member = new Member(new Address("성남시", "분당구", "판교동"), "kim", "010-1111-1111", "test",
                "test");

        //when
        Long saveId = memberService.join(member);

        //then
        assertEquals(member, memberService.findOne(saveId));
    }

    @Test
    public void 중복회원예외() {
        //given
        Member member1 = new Member(new Address("성남시", "분당구", "판교동"), "kim", "010-1111-1111",
                "test", "test1");
        Member member2 = new Member(new Address("성남시", "분당구", "판교동"), "lee", "010-2222-2222",
                "test", "test2");

        //when, then
        assertThrows(IllegalStateException.class, () -> {
            memberService.join(member1);
            memberService.join(member2);
        });
    }
}
