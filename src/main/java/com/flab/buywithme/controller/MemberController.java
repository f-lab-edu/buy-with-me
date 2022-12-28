package com.flab.buywithme.controller;

import com.flab.buywithme.domain.Address;
import com.flab.buywithme.domain.Member;
import com.flab.buywithme.dto.MemberSignInDTO;
import com.flab.buywithme.dto.MemberSignUpDTO;
import com.flab.buywithme.error.CustomException;
import com.flab.buywithme.error.ErrorCode;
import com.flab.buywithme.service.MemberService;
import com.flab.buywithme.utils.HashingUtil;
import com.flab.buywithme.utils.SessionConst;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public void signUp(@Valid @RequestBody MemberSignUpDTO signUpDto) {

        Address address = new Address(signUpDto.getDepth1(), signUpDto.getDepth2(),
                signUpDto.getDepth3());

        Member member = new Member(address, signUpDto.getName(), signUpDto.getPhoneNo(),
                signUpDto.getLoginId(), HashingUtil.encrypt(signUpDto.getPassword()));

        memberService.createMember(member);
    }

    @PostMapping("/signin")
    public void signIn(@Valid @RequestBody MemberSignInDTO signInDTO, HttpServletRequest request) {

        Member loginMember = memberService.signIn(signInDTO.getLoginId(),
                HashingUtil.encrypt(signInDTO.getPassword()));

        if (loginMember == null) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }

        HttpSession httpSession = request.getSession();
        httpSession.setAttribute(SessionConst.LOGIN_MEMBER, loginMember.getId());
    }

    @PostMapping("/signout")
    public void signOut(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

}
