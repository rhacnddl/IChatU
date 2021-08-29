package com.gorany.ichatu.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ChatDTO {

    //chat
    @ApiModelProperty(example = "채팅 ID")
    private Long id;
    @ApiModelProperty(example = "채팅 내용")
    private String content;
    @ApiModelProperty(example = "채팅 생성 날짜")
    private LocalDateTime regDate;

    //member
    @ApiModelProperty(example = "발신자 ID", dataType = "Long")
    private Long memberId;
    @ApiModelProperty(example = "발신자 닉네임", dataType = "String")
    private String nickname;

    //profile
    @ApiModelProperty(example = "발신자 프로필 ID", dataType = "Long")
    private Long profileId;
    @ApiModelProperty(example = "발신자 프로필 파일명", dataType = "String")
    private String profileName;
    @ApiModelProperty(example = "발신자 프로필 경로", dataType = "String")
    private String profilePath;

    //chatRoom
    @ApiModelProperty(example = "채팅방 ID", dataType = "Long")
    private Long chatRoomId;
    @ApiModelProperty(example = "채팅방 이름", dataType = "String")
    private String chatRoomName;
}
