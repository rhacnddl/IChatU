package com.gorany.ichatu.dto;

import com.gorany.ichatu.domain.Member;
import com.gorany.ichatu.domain.Profile;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberProfileDTO{

    @ApiModelProperty(value = "사용자 ID")
    @NotNull
    private Long id;

    @ApiModelProperty(value = "사용자 닉네임")
    private String nickname;
    @ApiModelProperty(value = "사용자 E-mail")
    private String email;

    @ApiModelProperty(value = "프로필 ID")
    private String profileId;
    @ApiModelProperty(value = "프로필 파일명")
    private String name;
    @ApiModelProperty(value = "프로필 파일 경로")
    private String path;

    public static MemberProfileDTO createMemberProfileDTO(Member member){

        Profile profile = member.getProfile();

        return MemberProfileDTO.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .profileId(profile != null? profile.getId() : null)
                .name(profile != null? profile.getName() : null)
                .path(profile != null? profile.getPath() : null)
                .build();
    }
}