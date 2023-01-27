package com.flab.buywithme.controller;

import com.flab.buywithme.service.EnrollService;
import com.flab.buywithme.utils.SessionConst;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequestMapping("/posts/{postId}/enrolls")
@RequiredArgsConstructor
public class EnrollController {

    private final EnrollService enrollService;

    @PostMapping
    public void join(@PathVariable Long postId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId) {
        enrollService.joinBuying(postId, memberId);
    }

    @DeleteMapping
    public void cancel(@PathVariable Long postId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long memberId) {
        enrollService.cancelJoining(postId, memberId);
    }
}
