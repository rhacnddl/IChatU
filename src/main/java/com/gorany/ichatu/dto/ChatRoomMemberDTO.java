package com.gorany.ichatu.dto;

import com.gorany.ichatu.domain.Join;
import com.gorany.ichatu.domain.Member;
import com.gorany.ichatu.domain.Profile;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
public class ChatRoomMemberDTO {

    @ApiModelProperty(example = "멤버 ID")
    private Long memberId;
    @ApiModelProperty(example = "멤버 닉네임")
    private String nickname;

    @ApiModelProperty(example = "멤버의 프로필 ID")
    private String profileId;
    @ApiModelProperty(example = "멤버의 프로필 파일명")
    private String profileName;
    @ApiModelProperty(example = "멤버의 프로필 경로")
    private String profilePath;

    public static ChatRoomMemberDTO createChatRoomMemberDTO(Join j){

        Member m = j.getMember();
        Profile p = m.getProfile();

        return ChatRoomMemberDTO.builder()
                .memberId(m.getId())
                .nickname(m.getNickname())
                .profileId(p != null? p.getId(): null)
                .profileName(p != null? p.getName(): null)
                .profilePath(p != null? p.getPath(): null)
                .build();
    }
}
