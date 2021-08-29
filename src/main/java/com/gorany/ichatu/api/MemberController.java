package com.gorany.ichatu.api;

import com.gorany.ichatu.dto.MemberDTO;
import com.gorany.ichatu.service.MemberService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/members")
@Log4j2
@CrossOrigin("*")
public class MemberController {

    private final MemberService memberService;
    //private final ProfileService profileService;

    @PutMapping("/info")
    @ApiOperation(value = "유저 정보 변경", notes = "유저의 정보(E-mail, Profile, ...)을 변경한다.")
    public ResponseEntity<String> updateMemberInfo(@RequestParam("file") MultipartFile file, MemberDTO memberDTO){

        if(file != null){
            /* 프로필 업로드 Logic */
        }



        return new ResponseEntity<>("success", HttpStatus.OK);
    }
}
