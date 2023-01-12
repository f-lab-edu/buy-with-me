package com.flab.buywithme.service;

import com.flab.buywithme.domain.Address;
import com.flab.buywithme.domain.Member;
import com.flab.buywithme.error.CustomException;
import com.flab.buywithme.error.ErrorCode;
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
        return memberRepository.save(member).getId();
    }

    private void checkDuplicateMemberExists(Member member) {
        Optional<Member> findMember = memberRepository.findByLoginId(member.getLoginId());
        if (findMember.isPresent()) {
            throw new CustomException(ErrorCode.MEMBER_ALREADY_EXIST);
        }
    }

    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public Member signIn(String loginId, String password) {
        return memberRepository.findByLoginId(loginId).stream()
                .filter(m -> m.getPassword().equals(password))
                .findAny()
                .orElse(null);
    }

    private Optional<Address> checkDuplicateAddressExists(Address address) {
        return addressRepository.findByDepth1AndDepth2AndDepth3(address.getDepth1(),
                address.getDepth2(), address.getDepth3());
    }
}
