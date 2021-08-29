package com.gorany.ichatu.dto;

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
    @ApiModelProperty(example = "알림 ID")
    private Long id;
    @ApiModelProperty(example = "채팅방 ID or 게시글 ID")
    private Long targetId;
    @ApiModelProperty(example = "알림 종류")
    private NotificationType type;
    @ApiModelProperty(example = "등록일")
    private LocalDateTime regDate;
    @ApiModelProperty(example = "확인 여부")
    private Character confirm;

    //sender
    @ApiModelProperty(example = "발신자 ID")
    private Long senderId;
    @ApiModelProperty(example = "발신자 닉네임")
    private String senderNickname;

    //profile
    @ApiModelProperty(example = "발신자 프로필 ID")
    private Long senderProfileId;
    @ApiModelProperty(example = "발신자 프로필 이름")
    private String senderProfileName;
    @ApiModelProperty(example = "발신자 프로필 경로")
    private String senderProfilePath;

    //receiver
    @ApiModelProperty(example = "수신자 ID")
    private Long receiverId;
    @ApiModelProperty(example = "수신자 닉네임")
    private String receiverNickname;
    @ApiModelProperty(example = "FCM 토큰")
    private String token;
}
