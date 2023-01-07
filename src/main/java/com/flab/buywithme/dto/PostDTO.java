package com.flab.buywithme.dto;

import java.time.LocalDateTime;
import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDTO {

    @NotBlank(message = "필수값입니다")
    @Size(max = 100)
    private String title;

    @NotBlank(message = "필수값입니다")
    @Size(max = 500)
    private String content;

    @NotNull(message = "필수값입니다")
    @Min(1)
    private int targetNo;

    @NotNull(message = "필수값입니다")
    @Future
    private LocalDateTime expiration;
}
