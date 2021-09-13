package com.gorany.ichatu.api;

import com.gorany.ichatu.domain.ChatRoom;
import com.gorany.ichatu.dto.ChatRoomDTO;
import com.gorany.ichatu.repository.ChatRoomRepository;
import com.gorany.ichatu.service.ChatRoomService;
import com.gorany.ichatu.service.JoinService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping(value = "/api/v1/rooms")
@CrossOrigin("*")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final JoinService joinService;

    @GetMapping(value = "/{regionId}/list/{page}")
    @ApiOperation(value = "채팅방 목록 조회", notes = "지역별 채팅방 목록을 조회한다.")
    public ResponseEntity<List<ChatRoomDTO>> getRooms(@PathVariable("regionId") Long regionId){

        List<ChatRoomDTO> rooms = chatRoomService.getRooms();

        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "특정 채팅방 조회", notes = "특정 채팅방의 데이터를 상세 조회한다.")
    public ResponseEntity<ChatRoomDTO> getRoom(@PathVariable("id") Long chatRoomId){

        ChatRoomDTO room = chatRoomService.getRoom(chatRoomId);

        return new ResponseEntity<>(room, HttpStatus.OK);
    }

    @PostMapping(value = "")
    @ApiOperation(value = "채팅방 개설", notes = "채팅방을 개설한다.")
    public ResponseEntity<Long> insertRoom(@RequestBody ChatRoomDTO chatRoomDTO){

        chatRoomDTO.setRegionId(1L);
        Long chatRoomId = chatRoomService.addRoom(chatRoomDTO);

        return new ResponseEntity<>(chatRoomId, HttpStatus.OK);
    }

    @GetMapping("/member/{memberId}")
    @ApiOperation(value = "가입한 채팅방 목록 조회", notes = "내가 가입한 채팅방들의 목록을 조회한다.")
    public ResponseEntity<List<ChatRoomDTO>> getMyRooms(@PathVariable("memberId") Long memberId){

        log.info("#ChatRoomController -> getMyRooms(Long) ", memberId);

        List<ChatRoomDTO> rooms = chatRoomService.getRoomsOnAside(memberId);

        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }
}
