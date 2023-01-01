package com.flab.buywithme.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberSignUpDTO {

    @NotBlank(message = "필수값입니다")
    @Size(max = 10)
    private String name;

    @NotBlank(message = "필수값입니다")
    @Size(max = 20)
    private String phoneNo;

    @NotBlank(message = "필수값입니다")
    @Size(max = 20)
    private String loginId;

    @NotBlank(message = "필수값입니다")
    @Size(max = 20)
    private String password;

    @NotBlank(message = "필수값입니다")
    @Size(max = 10)
    private String depth1;

    @NotBlank(message = "필수값입니다")
    @Size(max = 10)
    private String depth2;

    @NotBlank(message = "필수값입니다")
    @Size(max = 10)
    private String depth3;
}
