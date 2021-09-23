package com.gorany.ichatu.api;

import com.gorany.ichatu.domain.ChatRoom;
import com.gorany.ichatu.dto.AsideChatRoomDTO;
import com.gorany.ichatu.dto.ChatRoomDTO;
import com.gorany.ichatu.dto.ChatRoomMemberDTO;
import com.gorany.ichatu.repository.ChatRoomRepository;
import com.gorany.ichatu.service.ChatRoomService;
import com.gorany.ichatu.service.JoinService;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/api/v1/rooms")
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
    @ApiOperation(value = "가입한 채팅방 목록 조회", notes = "내가 가입한 채팅방들의 목록을 조회한다. ASIDE에 들어갈 내용")
    public ResponseEntity<List<AsideChatRoomDTO>> getMyRooms(@PathVariable("memberId") Long memberId){

        log.info("#ChatRoomController -> getMyRooms(Long) ", memberId);

        List<AsideChatRoomDTO> rooms = chatRoomService.getRoomsOnAside(memberId);

        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @DeleteMapping("/{chatRoomId}/member/{memberId}")
    @ApiOperation(value = "채팅방 삭제", notes = "방장이 채팅방을 삭제한다. 관련된 알림과 채팅도 함께 삭제된다.")
    public ResponseEntity<Long> removeChatRoom(@PathVariable("chatRoomId") Long chatRoomId, @PathVariable("memberId") Long memberId){

        /* Requester == Owner인지 Check Validation */
        if(!chatRoomService.isOwner(chatRoomId, memberId)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Long id = chatRoomService.removeRoom(chatRoomId);

        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @GetMapping("/{chatRoomId}/users")
    @ApiOperation(value = "채팅방에 가입한 멤버 목록 조회", notes = "특정 채팅방에 가입한 멤버 목록을 프로필과 함께 조회한다.")
    public ResponseEntity<ChatRoomMemberResponse> getMembersJoiningChatRoom(@PathVariable("chatRoomId") Long id){

        log.info("#ChatRoom Controller -> getMembersJoiningChatRoom(Long) : {}", id);
        List<ChatRoomMemberDTO> result = chatRoomService.getMembersOnChatRoom(id);

        return new ResponseEntity<>(new ChatRoomMemberResponse(result), HttpStatus.OK);
    }

    @Getter
    static class ChatRoomMemberResponse{

        @ApiModelProperty(example = "채팅방에 가입한 멤버 목록")
        private List<ChatRoomMemberDTO> members;
        @ApiModelProperty(example = "채팅방에 가입한 멤버 수")
        private int count;

        private ChatRoomMemberResponse(List<ChatRoomMemberDTO> members) {
            this.members = members;
            this.count = members.size();
        }
    }
}
