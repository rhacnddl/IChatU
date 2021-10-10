package com.gorany.ichatu.api;

import com.gorany.ichatu.domain.Role;
import com.gorany.ichatu.dto.*;
import com.gorany.ichatu.service.MemberService;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/members")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @DeleteMapping("/drop")
    @ApiOperation(value = "유저 탈퇴", notes = "유저의 정보를 파기하고, 탈퇴한다.")
    public ResponseEntity<String> deleteMember(@RequestBody @Valid MemberDropRequest request){

        log.info("#MemberController -> deleteMember(MemberDropRequest)");
        Boolean result = memberService.dropMember(request);

        return result? new ResponseEntity<>("success", HttpStatus.OK)
                :new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PatchMapping("/password")
    @ApiOperation(value = "유저 비밀번호 변경", notes = "유저의 비밀번호를 변경한다. 비밀번호는 다른 정보와 별도로 변경된다.")
    public ResponseEntity<String> updatePassword(@RequestBody @Valid MemberPasswordRequest request){

        log.info("#MemberController -> updatePassword(MemberPasswordRequest)");
        /* 비밀번호 변경 -> 기존 비밀번호 Check -> true? 변경, false? BAD_REQUEST */
        Boolean result = memberService.updatePassword(request);

        return result? new ResponseEntity<>("success", HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/{memberId}")
    @ApiOperation(value = "유저 정보 조회", notes = "유저의 정보(E-mail, Profile, ...)를 조회한다.")
    public ResponseEntity<MemberProfileDTO> getMemberInfo(@PathVariable(value = "memberId", required = true) Long memberId){

        log.info("#MemberController -> getMemberInfo(), memberId : {}", memberId);
        return new ResponseEntity<>(memberService.getMemberInfo(memberId), HttpStatus.OK);
    }

    @PutMapping("/info")
    @ApiOperation(value = "유저 정보 변경", notes = "유저의 정보(E-mail, Profile, ...)를 변경한다.")
    public ResponseEntity<String> updateMemberInfo(@Valid @RequestBody MemberProfileDTO memberProfileDTO){

        ProfileDTO profileDTO = null;

        if(memberProfileDTO.getName() != null && !memberProfileDTO.getName().equals(""))
            profileDTO = ProfileDTO.builder()
                    .profileId(memberProfileDTO.getProfileId())
                    .name(memberProfileDTO.getName())
                    .path(memberProfileDTO.getPath())
                    .build();

        MemberDTO memberDTO = MemberDTO.builder()
                .id(memberProfileDTO.getId())
                .nickname(memberProfileDTO.getNickname())
                .email(memberProfileDTO.getEmail())
                .build();

        /* 프로필 업로드 Logic */
        if(profileDTO != null)
            memberDTO.setProfileDTO(profileDTO);

        memberService.updateMember(memberDTO);

        return new ResponseEntity<>("success", HttpStatus.OK);
    }

}
