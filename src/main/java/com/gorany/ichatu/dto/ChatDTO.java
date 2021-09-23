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
    @ApiModelProperty(value = "채팅 ID", example = "1")
    private Long id;
    @ApiModelProperty(value = "채팅 내용", example = "Hello World")
    private String content;
    @ApiModelProperty(value = "채팅 생성 날짜", example = "2021-09-23 18:12:23")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDate;

    //member
    @ApiModelProperty(value = "발신자 ID", dataType = "Long", example = "1")
    private Long memberId;
    @ApiModelProperty(value = "발신자 닉네임", dataType = "String", example = "gorany")
    private String nickname;

    //profile
//    @ApiModelProperty(value = "발신자 프로필 ID", dataType = "Long")
//    private Long profileId;

    @ApiModelProperty(value = "발신자 프로필 ID", dataType = "Long")
    private String profileId;
    @ApiModelProperty(value = "발신자 프로필 파일명", dataType = "String")
    private String profileName;
    @ApiModelProperty(value = "발신자 프로필 경로", dataType = "String")
    private String profilePath;

    //chatRoom
    @ApiModelProperty(value = "채팅방 ID", dataType = "Long")
    private Long chatRoomId;
    @ApiModelProperty(value = "채팅방 이름", dataType = "String")
    private String chatRoomName;
}
