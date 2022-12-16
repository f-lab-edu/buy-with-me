package com.flab.buywithme.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.flab.buywithme.domain.Address;
import com.flab.buywithme.domain.Member;
import com.flab.buywithme.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Test
    public void 회원가입() {
        //given
        Member member = fakeMember();

        Long fakeMemberId = 1L;
        ReflectionTestUtils.setField(member, "id", fakeMemberId);

        //mocking
        given(memberRepository.save(any()))
                .willReturn(member);
        given(memberRepository.findById(fakeMemberId))
                .willReturn(Optional.ofNullable(member));

        //when
        Long saveId = memberService.join(member);

        //then
        assertEquals(member, memberService.findById(saveId).orElse(null));
    }

    @Test
    public void 중복회원예외() {
        //given
        Member member1 = fakeMember();
        Member member2 = fakeMember();

        //mocking
        given(memberRepository.save(any()))
                .willReturn(member1)
                .willThrow(IllegalStateException.class);

        //when, then
        assertThrows(IllegalStateException.class, () -> {
            memberService.join(member1);
            memberService.join(member2);
        });
    }

    private Member fakeMember() {
        return new Member(new Address("성남시", "분당구", "판교동"), "kim", "010-1111-1111",
                "test", "test1");
    }

}
