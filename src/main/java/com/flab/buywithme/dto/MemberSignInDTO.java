package com.flab.buywithme.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberSignInDTO {

    @NotBlank
    @Size(max = 20)
    private String loginId;

    @NotBlank
    @Size(max = 20)
    private String password;
}
