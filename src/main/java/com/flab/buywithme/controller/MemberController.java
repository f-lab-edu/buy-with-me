package com.flab.buywithme.controller;

import com.flab.buywithme.domain.Address;
import com.flab.buywithme.domain.Member;
import com.flab.buywithme.dto.MemberSignUpDTO;
import com.flab.buywithme.service.MemberService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public void signUp(@Valid @RequestBody MemberSignUpDTO signUpDto, BindingResult result) {

        if (result.hasErrors()) {
            throw new IllegalStateException("올바르지 않은 입력입니다");
        }

        Address address = new Address(signUpDto.getDepth1(), signUpDto.getDepth2(),
                signUpDto.getDepth3());

        Member member = new Member(address, signUpDto.getName(), signUpDto.getPhoneNo(),
                signUpDto.getLoginId(),
                signUpDto.getPassword());

        memberService.join(member);
    }

}
