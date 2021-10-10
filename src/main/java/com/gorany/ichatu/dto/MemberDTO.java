package com.gorany.ichatu.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gorany.ichatu.domain.Role;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MemberDTO {

    @ApiModelProperty(value = "사용자 ID")
    private Long id;

    @ApiModelProperty(value = "사용자 PASSWORD")
    private String password;
    @ApiModelProperty(value = "사용자 닉네임")
    private String nickname;
    @ApiModelProperty(value = "사용자 E-mail")
    private String email;

    @ApiModelProperty(value = "최근 접속일")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loginDate;
    @ApiModelProperty(value = "사용자 가입일")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDate;

    @ApiModelProperty(value = "이용 가능 여부")
    private Boolean available;

    @ApiModelProperty(value = "권한")
    private Role role;

    @ApiModelProperty(value = "사용자 프로필")
    private ProfileDTO profileDTO;
    @ApiModelProperty(value = "사용자 알림 목록")
    @Builder.Default
    private List<NotificationDTO> notificationDTOList = new ArrayList<>();

    @ApiModelProperty(value = "사용자 토큰")
    private String token;
}
