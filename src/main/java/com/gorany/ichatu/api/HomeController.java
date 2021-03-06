package com.gorany.ichatu.api;

import com.gorany.ichatu.dto.MemberDTO;
import com.gorany.ichatu.service.MemberService;
import com.gorany.ichatu.storage.TokenStorage;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping(value = "/api/v1")
@RequiredArgsConstructor
public class HomeController {

    private static final TokenStorage storage = TokenStorage.getInstance();
    private final Map<Long, String> map = storage.getMap();
    private final MemberService memberService;

    @PostMapping(value = "/signup")
    @ApiOperation(value = "회원 가입", notes = "회원 가입을 진행하기 위한 데이터 입력")
    public ResponseEntity<Long> signup(@RequestBody MemberDTO memberDTO){

        log.info(memberDTO.toString());
        /* 1. validation (중복된 닉네임이면 -1 return) */
        /* 2. data -> DB (sign-up) */
        Long memberId = memberService.signup(memberDTO);
        /* 3. return Member_ID */
        return new ResponseEntity<>(memberId, HttpStatus.OK);
    }

    @PostMapping("/login")
    @ApiOperation(value = "로그인", notes = "로그인을 위한 데이터 입력")
    public ResponseEntity<Object> loginPost(@RequestBody MemberDTO memberDTO){

        String nickname = memberDTO.getNickname();
        String password = memberDTO.getPassword();
        String token = memberDTO.getToken();

        /* request for login
        *  if success -> save TOKEN
        *  else -> do nothing
        * */
        MemberDTO result = memberService.login(memberDTO);

        if(result != null){ //로그인 성공
            map.put(result.getId(), token);

            log.info(String.format("[LOGIN SUCCESS] => nickname : %s, password : %s, token : %s", nickname, password, token));

            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        else{ //로그인 실패
            log.info(String.format("[LOGIN FAILED] => nickname : %s, password : %s, token : %s", nickname, password, token));
            return new ResponseEntity<>("fail", HttpStatus.OK);
        }

    }

    @PostMapping("/logout")
    @ApiOperation(value = "로그아웃", notes = "로그아웃을 위한 데이터 입력")
    public ResponseEntity<String> logout(@RequestBody MemberDTO memberDTO){

        String nickname = memberDTO.getNickname();
        Long memberId = memberDTO.getId();

        /* remove()는 성공 시 return <value>. 실패 시 return null */
        String token = map.remove(memberId);

        if(token == null) {
            return new ResponseEntity<>("fail", HttpStatus.BAD_REQUEST);
        }

        log.info("nickname : " + nickname + " 님이 로그아웃 했습니다.");

        return new ResponseEntity<>("success", HttpStatus.OK);
    }

//    @GetMapping(value = "/session")
//    public ResponseEntity<MemberDTO> getSessionData(){
//
//        Long memberId = (Long) session.getAttribute("memberId");
//        String nickname = (String) session.getAttribute("nickname");
//        String token = map.get(memberId);
//
//        System.out.println(memberId);
//        System.out.println(nickname);
//        System.out.println(token);
//
//        if(token == null)
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//
//        MemberDTO memberDTO = MemberDTO.builder().id(memberId).nickname(nickname).token(token).build();
//
//        return new ResponseEntity<>(memberDTO, HttpStatus.OK);
//    }
}