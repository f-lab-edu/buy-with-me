package com.flab.buywithme.controller;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberForm {

    @NotBlank(message = "이름은 필수 값입니다")
    @Size(max = 10)
    private String name;

    @NotBlank(message = "전화번호는 필수 값입니다")
    @Size(max = 20)
    private String phoneNo;

    @NotBlank(message = "로그인 아이디는 필수 값입니다")
    @Size(max = 20)
    private String loginId;

    @NotBlank(message = "비밀번호는 필수 값입니다")
    @Size(max = 20)
    private String password;

    @NotBlank(message = "거주 시는 필수 값입니다")
    @Size(max = 10)
    private String depth1;

    @NotBlank(message = "거주 구는 필수 값입니다")
    @Size(max = 10)
    private String depth2;

    @NotBlank(message = "거주 동은 필수 값입니다")
    @Size(max = 10)
    private String depth3;
}
