package com.gorany.ichatu.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileDTO {

//    @ApiModelProperty(value = "프로필 ID")
//    private Long profileId;

    @ApiModelProperty(value = "프로필 ID")
    private String profileId;

    @ApiModelProperty(value = "프로필 파일명")
    private String name;
    @ApiModelProperty(value = "프로필 파일 경로")
    private String path;

    //member
    @ApiModelProperty(value = "프로필 소유자 ID")
    private Long memberId;
}
