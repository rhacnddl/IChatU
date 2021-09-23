package com.gorany.ichatu.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class MemberDropRequest {

    @NotNull
    @ApiModelProperty(value = "멤버 ID")
    private Long id;

    @NotNull
    @ApiModelProperty(value = "멤버의 기존 비밀번호")
    private String password;

    @ApiModelProperty(value = "설문조사 첫번째")
    private int first;
    @ApiModelProperty(value = "설문조사 두번째")
    private int second;
}
