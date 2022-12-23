package com.flab.buywithme.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.flab.buywithme.domain.Address;
import com.flab.buywithme.domain.Member;
import com.flab.buywithme.repository.AddressRepository;
import com.flab.buywithme.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private AddressRepository addressRepository;

    @Test
    @DisplayName("회원가입 성공")
    public void signupSuccess() {
        //given
        Member member = fakeMember(1L);

        //mocking
        given(memberRepository.save(any(Member.class)))
                .willReturn(member);
        given(memberRepository.findById(member.getId()))
                .willReturn(Optional.ofNullable(member));

        //when
        Long saveId = memberService.createMember(member);

        //then
        assertEquals(member, memberService.findById(saveId).orElse(null));
    }

    @Test
    @DisplayName("이미 존재하는 LoginId로 회원 가입 시도할 때 실패")
    public void existingLoginIdFail() {
        //given
        Member member = fakeMember(1L);

        //mocking
        given(memberRepository.findByLoginId(member.getLoginId()))
                .willReturn(Optional.ofNullable(member));

        //when, then
        assertThrows(IllegalStateException.class, () -> memberService.createMember(member));
    }

    @Test
    @DisplayName("동일 LoginId로 중복 회원 가입 시도할 때 실패")
    public void duplicateSignupFail() {
        //given
        Member member1 = fakeMember(1L);
        Member member2 = fakeMember(2L);

        //mocking
        given(memberRepository.save(any(Member.class)))
                .willReturn(member1)
                .willThrow(DataIntegrityViolationException.class);

        //when, then
        assertThrows(DataIntegrityViolationException.class, () -> {
            memberService.createMember(member1);
            memberService.createMember(member2);
        });
    }

    private Member fakeMember(Long memberId) {
        Member member = new Member(new Address("성남시", "분당구", "판교동"), "kim", "010-1111-1111",
                "test", "test1");
        member.setId(memberId);
        return member;
    }

}
