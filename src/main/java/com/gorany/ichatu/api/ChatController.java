package com.gorany.ichatu.api;

import com.gorany.ichatu.dto.ChatDTO;
import com.gorany.ichatu.service.ChatService;
import com.gorany.ichatu.storage.BufferStorage;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
@Slf4j
public class ChatController {

    private final ChatService chatService;

    private final BufferStorage bufferStorage = BufferStorage.getInstance();
    private Map<Long, List<ChatDTO>> bufferMap;

    @PostConstruct
    private void init(){
        this.bufferMap = bufferStorage.getBufferMap();
    }

    /**
     * 채팅 내역 조회 V1
     * @deprecated
     * @param chatRoomId 채팅방 ID
     * @param memberId 요청자 ID
     * @param page 페이지 정보
     * @return ResponseEntity<ChatResponse> 채팅 응답 내역
     * */
    @Deprecated
    @GetMapping(value = "/v1/chats/{chatRoomId}/member/{memberId}")
    @ApiOperation(value = "채팅 내역 조회", notes = "유저 A가 채팅방 B의 채팅 내역을 페이징하여 조회한다. (단, 채팅방 참여 이후 데이터만 조회)")
    public ResponseEntity<ChatResponse> loadChats(@PathVariable("chatRoomId") Long chatRoomId,
                                                   @PathVariable("memberId") Long memberId,
                                                   @RequestParam(name = "page", defaultValue = "1") Integer page){

        if(chatRoomId == null || memberId == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        log.info("#ChatController -> ChatService.getChats(Long, Long, Integer)");

        List<ChatDTO> chats  = chatService.getChats(chatRoomId, memberId, page);
        Collections.reverse(chats);

        if(bufferMap.containsKey(chatRoomId) && page == 1){
            List<ChatDTO> bufferResult = bufferMap.get(chatRoomId);
            chats.addAll(bufferResult);
        }

        return new ResponseEntity<>(new ChatResponse(chats, chats.size()), HttpStatus.OK);
    }

    /**
     * 채팅 내역 조회 V2
     * - content : 채팅 데이터
     * - empty : 비어있는지 여부
     * - first : 첫번째 페이지 여부
     * - last : 마지막 페이지 여부
     * - number : 현재 페이지
     * - numberOfElements : 데이터 갯수
     * - pageable : 페이징 및 정렬 정보
     * - size : 가져와야할 데이터
     * - sort : 정렬 정보
     * @param chatRoomId 채팅방 ID
     * @param memberId 요청자 ID
     * @param page 페이지 정보
     * @return ResponseEntity<ChatResponseV2> 채팅 내역
     * */
    @GetMapping(value = "/v2/chats/{chatRoomId}/member/{memberId}")
    @ApiOperation(value = "채팅 내역 조회 V2",
            notes = "유저 A가 채팅방 B의 채팅 내역을 페이징하여 조회한다. (단, 채팅방 참여 이후 데이터만 조회)")
    public ResponseEntity<ChatResponseV2> loadChatsV2(@PathVariable("chatRoomId") Long chatRoomId,
                                                  @PathVariable("memberId") Long memberId,
                                                  @RequestParam(name = "page", defaultValue = "1") Integer page){

        if(chatRoomId == null || memberId == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        log.info("#ChatController -> ChatService.getChats(Long, Long, Integer)");

        Slice<ChatDTO> chats = chatService.getChatsV2(chatRoomId, memberId, page);
        ChatResponseV2 response = new ChatResponseV2(new ArrayList<>(chats.getContent()),
                chats.getNumberOfElements(),
                chats.isEmpty(),
                chats.isFirst(),
                chats.isLast(),
                chats.getNumber());
        Collections.reverse(response.getContent());

        if(bufferMap.containsKey(chatRoomId) && page == 1){
            List<ChatDTO> bufferResult = bufferMap.get(chatRoomId);
            response.getContent().addAll(bufferResult);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Data
    @AllArgsConstructor
    private static class ChatResponse{
        @ApiModelProperty(value = "채팅 목록")
        private List<ChatDTO> chats;

        @ApiModelProperty(value = "채팅 목록의 개수")
        private int count;
    }
    @Data
    @AllArgsConstructor
    private static class ChatResponseV2{
        @ApiModelProperty(value = "채팅 목록")
        private List<ChatDTO> content;
        @ApiModelProperty(example = "채팅 목록의 개수")
        private int numberOfElements;
        @ApiModelProperty(value = "비어있는지 여부")
        private boolean empty;
        @ApiModelProperty(value = "첫번째 페이지 여부")
        private boolean first;
        @ApiModelProperty(value = "마지막 페이지 여부")
        private boolean last;
        @ApiModelProperty(value = "현재 페이지")
        private int number;
    }
}
