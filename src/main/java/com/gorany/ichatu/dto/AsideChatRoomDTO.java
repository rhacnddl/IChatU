package com.gorany.ichatu.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AsideChatRoomDTO {

    //chatRoom
    @ApiModelProperty(value = "채팅방 ID")
    private Long id;
    @ApiModelProperty(value = "채팅방 제목")
    private String name;
    @ApiModelProperty(value = "채팅방 개설일자")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDate;

    //member
    @ApiModelProperty(value = "채팅방 만든 사람 ID")
    private Long memberId;
    @ApiModelProperty(value = "채팅방 만든 사람 닉네임")
    private String nickname;

    @ApiModelProperty(value = "채팅방 만든 사람 프로필 ID")
    private String profileId;
    @ApiModelProperty(value = "채팅방 만든 사람 프로필 파일명")
    private String profileName;
    @ApiModelProperty(value = "채팅방 만든 사람 프로필 경로")
    private String profilePath;

    //region
    @ApiModelProperty(value = "채팅방이 속한 지역 ID")
    private Long regionId;
    //city

    @ApiModelProperty(value = "읽지 않은 채팅 개수")
    private Long cnt;

    @ApiModelProperty(value = "가장 최근 채팅 내용")
    private String content;
    @ApiModelProperty(value = "가장 최근 채팅 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime contentRegDate;

    public static AsideChatRoomDTO createDtoByObj(Object[] o){
        AsideChatRoomDTO dto = new AsideChatRoomDTO();
        dto.id = o[0] != null? Long.parseLong(String.valueOf(o[0])) : null;
        dto.name = o[1] != null? (String) o[1] : null;
        dto.regDate = o[2] != null? ((Timestamp)o[2]).toLocalDateTime() : null;
        dto.memberId = o[3] != null? Long.parseLong(String.valueOf(o[3])) : null;
        dto.nickname = o[4] != null? (String) o[4] : null;
        dto.profileId = o[5] != null? (String) o[5] : null;
        dto.profileName = o[6] != null? (String) o[6] : null;
        dto.profilePath = o[7] != null? (String) o[7] : null;
        dto.regionId = o[8] != null? Long.parseLong(String.valueOf(o[8])) : null;
        dto.cnt = o[9] != null? Long.parseLong(String.valueOf(o[9])) : null;
        dto.content = o[10] != null? (String) o[10] : null;
        dto.contentRegDate = o[11] != null? ((Timestamp) o[11]).toLocalDateTime() : null;

        return dto;
    }
}
