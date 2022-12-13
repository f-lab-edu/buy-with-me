package com.flab.buywithme.controller;

import com.flab.buywithme.domain.Address;
import com.flab.buywithme.domain.Member;
import com.flab.buywithme.service.MemberService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "/members/createMemberForm";
    }

    @ResponseBody
    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result) {

        if(result.hasErrors()) {
            return "/members/createMemberForm";
        }

        Address address = new Address(form.getDepth1(), form.getDepth2(), form.getDepth3());

        Member member = new Member(address, form.getName(), form.getPhoneNo(), form.getLoginId(),
                form.getPassword());

        memberService.join(member);
        return "ok";
    }

}
