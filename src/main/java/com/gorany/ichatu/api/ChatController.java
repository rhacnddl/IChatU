package com.gorany.ichatu.api;

import com.gorany.ichatu.dto.ChatDTO;
import com.gorany.ichatu.service.ChatService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/chats")
@Slf4j
@CrossOrigin({"https://ichatu.ga", "http://localhost:3000"})
public class ChatController {

    private final ChatService chatService;

    @GetMapping(value = "/{chatRoomId}/member/{memberId}")
    @ApiOperation(value = "채팅 내역 조회", notes = "유저 A가 채팅방 B의 채팅 내역을 페이징하여 조회한다. (단, 채팅방 참여 이후 데이터만 조회)")
    public ResponseEntity<List<ChatDTO>> loadChats(@PathVariable("chatRoomId") Long chatRoomId,
                                                   @PathVariable("memberId") Long memberId,
                                                   @RequestParam(name = "page", defaultValue = "1") Integer page){

        if(chatRoomId == null || memberId == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        log.info("#ChatController -> ChatService.getChats(Long, Long, Integer)");
        List<ChatDTO> chats = chatService.getChats(chatRoomId, memberId, page);
        Collections.reverse(chats);
        return new ResponseEntity<>(chats, HttpStatus.OK);
    }
}
