package com.gorany.ichatu.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gorany.ichatu.domain.Member;
import com.gorany.ichatu.domain.NotificationType;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NotificationDTO {

    //notification
    @ApiModelProperty(value = "알림 ID")
    private Long id;
    @ApiModelProperty(value = "채팅방 ID or 게시글 ID")
    private Long targetId;
    @ApiModelProperty(value = "알림 종류")
    private NotificationType type;
    @ApiModelProperty(value = "등록일")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDate;
    @ApiModelProperty(value = "확인 여부")
    private Character confirm;

    //sender
    @ApiModelProperty(value = "발신자 ID")
    private Long senderId;
    @ApiModelProperty(value = "발신자 닉네임")
    private String senderNickname;

    //profile
//    @ApiModelProperty(value = "발신자 프로필 ID")
//    private Long senderProfileId;

    @ApiModelProperty(value = "발신자 프로필 ID")
    private String senderProfileId;
    @ApiModelProperty(value = "발신자 프로필 이름")
    private String senderProfileName;
    @ApiModelProperty(value = "발신자 프로필 경로")
    private String senderProfilePath;

    //receiver
    @ApiModelProperty(value = "수신자 ID")
    private Long receiverId;
    @ApiModelProperty(value = "수신자 닉네임")
    private String receiverNickname;
    @ApiModelProperty(value = "FCM 토큰")
    private String token;
}
