package com.flab.buywithme.service;

import com.flab.buywithme.domain.Address;
import com.flab.buywithme.domain.Member;
import com.flab.buywithme.repository.AddressRepository;
import com.flab.buywithme.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final AddressRepository addressRepository;

    @Transactional
    public Long createMember(Member member) {
        checkDuplicateMemberExists(member);
        Optional<Address> address = checkDuplicateAddressExists(member.getAddress());
        address.ifPresent(member::setAddress);
        memberRepository.save(member);
        return member.getId();
    }

    private void checkDuplicateMemberExists(Member member) {
        Optional<Member> findMember = memberRepository.findByLoginId(member.getLoginId());
        if (findMember.isPresent()) {
            throw new IllegalStateException("이미 존재하는 회원입니다!");
        }
    }

    public Optional<Member> findById(Long memberId) {
        return memberRepository.findById(memberId);
    }

    public Optional<Address> checkDuplicateAddressExists(Address address) {
        return addressRepository.findByDepth1AndDepth2AndDepth3(address.getDepth1(),
                address.getDepth2(), address.getDepth3());
    }
}
