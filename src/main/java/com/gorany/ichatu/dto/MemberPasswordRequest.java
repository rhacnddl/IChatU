package com.gorany.ichatu.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberPasswordRequest {

    @NotNull
    @ApiModelProperty(value = "멤버 ID")
    private Long id;

    @NotNull
    @ApiModelProperty(value = "멤버의 기존 비밀번호")
    private String oldPassword;

    @NotNull
    @ApiModelProperty(value = "멤버의 새 비밀번호")
    private String newPassword;
}
