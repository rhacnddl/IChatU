package com.gorany.ichatu.api;

import com.gorany.ichatu.service.JoinService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/joins")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin({"https://ichatu.ga", "http://localhost:3000"})
public class JoinController {

    private final JoinService joinService;

    @GetMapping(value = "/{chatRoomId}/member/{memberId}")
    @ApiOperation(value = "채팅방 가입 조회", notes = "요청자가 채팅방에 가입했는지 여부를 조회한다.")
    public ResponseEntity<Boolean> getJoin(@PathVariable("chatRoomId") Long chatRoomId, @PathVariable("memberId") Long memberId){

        log.info("#JoinController -> getJoin({}, {})", chatRoomId, memberId);
        Boolean result = joinService.checkJoinMember(chatRoomId, memberId);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/{chatRoomId}/member/{memberId}")
    @ApiOperation(value = "채팅방 가입", notes = "요청자가 채팅방에 가입(참여)한다.")
    public ResponseEntity<Long> requestJoin(@PathVariable("chatRoomId") Long chatRoomId, @PathVariable("memberId") Long memberId){

        log.info("#JoinController -> requestJoin({}, {})", chatRoomId, memberId);
        Long joinId = joinService.joinChatRoom(chatRoomId, memberId);

        return new ResponseEntity<>(joinId, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{chatRoomId}/member/{memberId}")
    @ApiOperation(value = "채팅방 탈퇴", notes = "요청자가 채팅방을 탈퇴한다.")
    public ResponseEntity<Integer> dropJoin(@PathVariable("chatRoomId") Long chatRoomId, @PathVariable("memberId") Long memberId){

        /* 만약 delete 과정 중 Exception이 발생하면? */
        log.info("#JoinController -> dropJoin({}, {})", chatRoomId, memberId);
        Integer resultCount = joinService.dropChatRoom(chatRoomId, memberId);

        return new ResponseEntity<>(resultCount, HttpStatus.OK);
    }
}
