package com.flab.buywithme.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.flab.buywithme.domain.Address;
import com.flab.buywithme.domain.Member;
import com.flab.buywithme.repository.AddressRepository;
import com.flab.buywithme.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private AddressRepository addressRepository;

    private Member member;
    private Address address;

    @BeforeEach
    public void setup() {
        member = fakeMember(1L);
        address = member.getAddress();
    }

    @Test
    @DisplayName("loginId 중복 시 예외 발생")
    public void existingLoginIdFail() {
        //given
        given(memberRepository.findByLoginId(member.getLoginId()))
                .willReturn(Optional.of(member));

        //when, then
        assertThrows(IllegalStateException.class, () -> memberService.createMember(member));
    }

    @Test
    @DisplayName("회원가입 성공")
    public void signUpSuccess() {
        //given
        given(memberRepository.findByLoginId(member.getLoginId()))
                .willReturn(Optional.empty());
        given(addressRepository.findByDepth1AndDepth2AndDepth3(address.getDepth1(),
                address.getDepth2(), address.getDepth3()))
                .willReturn(Optional.empty());
        given(memberRepository.save(member))
                .willReturn(member);

        //when
        memberService.createMember(member);

        //then
        then(memberRepository).should().save(fakeMember(1L));
    }

    @Test
    @DisplayName("회원가입 시 기존에 address table에 존재하는 주소 조회 성공")
    public void addressLookupSuccess() {
        //given
        Address existingAddress = fakeAddress(1L);
        given(memberRepository.findByLoginId(member.getLoginId()))
                .willReturn(Optional.empty());
        given(addressRepository.findByDepth1AndDepth2AndDepth3(address.getDepth1(),
                address.getDepth2(), address.getDepth3()))
                .willReturn(Optional.of(existingAddress));
        given(memberRepository.save(member))
                .willReturn(member);

        //when
        memberService.createMember(member);

        //then
        Member expected = fakeMember(1L);
        expected.setAddress(fakeAddress(1L));
        then(memberRepository).should().save(expected);
    }

    private Member fakeMember(Long memberId) {
        Member member = new Member(new Address("성남시", "분당구", "판교동"), "kim", "010-1111-1111",
                "test", "test1");
        member.setId(memberId);
        return member;
    }

    private Address fakeAddress(Long addressId) {
        Address address = new Address("성남시", "분당구", "판교동");
        address.setId(addressId);
        return address;
    }

}
