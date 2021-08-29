package com.gorany.ichatu.api;

import com.gorany.ichatu.dto.MemberDTO;
import com.gorany.ichatu.service.MemberService;
import com.gorany.ichatu.storage.TokenStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Log4j2
@RequestMapping(value = "/api/v1")
@RequiredArgsConstructor
@CrossOrigin("*")
public class HomeController {

    private static final TokenStorage storage = TokenStorage.getInstance();
    private final Map<Long, String> map = storage.getMap();
    private final MemberService memberService;

    @PostMapping(value = "/signup")
    public ResponseEntity<Long> signup(@RequestBody MemberDTO memberDTO){
        log.info(memberDTO);
        /* 1. validation */

        /* 2. data -> DB (sign-up) */
        Long memberId = memberService.signup(memberDTO);
        /* 3. return Member_ID */
        return new ResponseEntity<>(memberId, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> loginPost(@RequestBody MemberDTO memberDTO){

        String nickname = memberDTO.getNickname();
        String password = memberDTO.getPassword();
        String token = memberDTO.getToken();

        System.out.println("nickname = " + nickname);
        System.out.println("password = " + password);
        System.out.println("token = " + token);

        /* request for login
        *  if success -> save TOKEN
        *  else -> do nothing
        * */
        MemberDTO result = memberService.login(memberDTO);
        if(result.getId() != null){
            map.put(result.getId(), token);

            log.info(String.format("[LOGIN SUCCESS] => nickname : %s, password : %s, token : %s", nickname, password, token));

            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        else{
            log.info(String.format("[LOGIN FAILED] => nickname : %s, password : %s, token : %s", nickname, password, token));
            return new ResponseEntity<>("fail", HttpStatus.OK);
        }

    }

    @PostMapping("/logout")
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