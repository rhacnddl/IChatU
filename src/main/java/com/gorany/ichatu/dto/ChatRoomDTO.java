package com.gorany.ichatu.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomDTO {

    //chatRoom
    @ApiModelProperty(example = "채팅방 ID")
    private Long id;
    @ApiModelProperty(example = "채팅방 제목")
    private String name;
    @ApiModelProperty(example = "채팅방 개설일자")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDate;

    //member
    @ApiModelProperty(example = "채팅방 만든 사람 ID")
    private Long memberId;
    @ApiModelProperty(example = "채팅방 만든 사람 닉네임")
    private String nickname;

    @ApiModelProperty(example = "채팅방 만든 사람 프로필 ID")
    private String profileId;
    @ApiModelProperty(example = "채팅방 만든 사람 프로필 파일명")
    private String profileName;
    @ApiModelProperty(example = "채팅방 만든 사람 프로필 경로")
    private String profilePath;

    //region
    @ApiModelProperty(example = "채팅방이 속한 지역 ID")
    private Long regionId;
    //city
}
