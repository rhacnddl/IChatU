package com.gorany.ichatu.api;

import com.gorany.ichatu.domain.Role;
import com.gorany.ichatu.dto.MemberDTO;
import com.gorany.ichatu.dto.NotificationDTO;
import com.gorany.ichatu.dto.ProfileDTO;
import com.gorany.ichatu.service.MemberService;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/members")
@Slf4j
@CrossOrigin({"https://ichatu.ga", "http://localhost:3000"})
public class MemberController {

    private final MemberService memberService;

    @PutMapping("/info")
    @ApiOperation(value = "유저 정보 변경", notes = "유저의 정보(E-mail, Profile, ...)을 변경한다.")
    public ResponseEntity<String> updateMemberInfo(@RequestBody MemberProfileDTO memberProfileDTO){

        ProfileDTO profileDTO = null;

        if(memberProfileDTO.getName() != null && !memberProfileDTO.getName().equals(""))
            profileDTO = ProfileDTO.builder().profileId(memberProfileDTO.getProfileId()).name(memberProfileDTO.getName()).path(memberProfileDTO.getPath()).build();

        MemberDTO memberDTO = MemberDTO.builder().id(memberProfileDTO.getId()).nickname(memberProfileDTO.getNickname()).email(memberProfileDTO.getEmail()).build();

        /* 프로필 업로드 Logic */
        if(profileDTO != null)
            memberDTO.setProfileDTO(profileDTO);
        System.out.println("memberDTO = " + memberDTO);
//        Long profileId = memberService.updateMember(memberDTO);
        String profileId = memberService.updateMember(memberDTO);

        return new ResponseEntity<>(profileId, HttpStatus.OK);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    private static class MemberProfileDTO{

        @ApiModelProperty(example = "사용자 ID")
        private Long id;

        @ApiModelProperty(example = "사용자 닉네임")
        private String nickname;
        @ApiModelProperty(example = "사용자 E-mail")
        private String email;

//        @ApiModelProperty(example = "프로필 ID")
//        private Long profileId;

        @ApiModelProperty(example = "프로필 ID")
        private String profileId;
        @ApiModelProperty(example = "프로필 파일명")
        private String name;
        @ApiModelProperty(example = "프로필 파일 경로")
        private String path;
    }
}
