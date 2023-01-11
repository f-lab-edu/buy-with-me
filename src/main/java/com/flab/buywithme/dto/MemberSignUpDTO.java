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

    @NotBlank
    @Size(max = 10)
    private String name;

    @NotBlank
    @Size(max = 20)
    private String phoneNo;

    @NotBlank
    @Size(max = 20)
    private String loginId;

    @NotBlank
    @Size(max = 20)
    private String password;

    @NotBlank
    @Size(max = 10)
    private String depth1;

    @NotBlank
    @Size(max = 10)
    private String depth2;

    @NotBlank
    @Size(max = 10)
    private String depth3;
}
