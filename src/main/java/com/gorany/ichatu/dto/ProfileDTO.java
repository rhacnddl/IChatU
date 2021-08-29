package com.gorany.ichatu.dto;

import io.swagger.annotations.ApiModelProperty;

public class ProfileDTO {

    @ApiModelProperty(example = "프로필 ID")
    private Long profileId;
    @ApiModelProperty(example = "프로필 파일명")
    private String name;
    @ApiModelProperty(example = "프로필 파일 경로")
    private String path;

    //member
    @ApiModelProperty(example = "프로필 소유자 ID")
    private Long memberId;
}
