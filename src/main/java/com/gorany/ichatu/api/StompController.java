package com.gorany.ichatu.api;

import com.gorany.ichatu.domain.Chat;
import com.gorany.ichatu.domain.Notification;
import com.gorany.ichatu.dto.ChatDTO;
import com.gorany.ichatu.dto.ProfileDTO;
import com.gorany.ichatu.service.ChatService;
import com.gorany.ichatu.service.NotificationService;
import com.gorany.ichatu.storage.BufferStorage;
import com.gorany.ichatu.storage.CheckStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin({"https://ichatu.ga", "http://localhost:3000"})
public class StompController {

    private final SimpMessagingTemplate template;
    private final RabbitTemplate rabbitTemplate;

    private final NotificationService notificationService;
    private final ChatService chatService;

    private final String EXCHANGE_NAME = "ex1";
    private final String CHAT_ROUTING_KEY = "chat.";

    private final static BufferStorage bufferStorage = BufferStorage.getInstance();
    private Map<Long, List<ChatDTO>> bufferMap;
    private final static CheckStorage checkStorage = CheckStorage.getInstance();
    private Map<Long, Set<Long>> checkMap;

    @PostConstruct
    private void init(){
        this.bufferMap = bufferStorage.getBufferMap();
        this.checkMap = checkStorage.getCheckMap();
    }


    @MessageMapping("/chat/enter")
    public void enter(ChatDTO chatDTO){

        Long mid = chatDTO.getMemberId();
        Long cid = chatDTO.getChatRoomId();

        if(checkMap.containsKey(cid)){
            checkMap.get(cid).add(mid);
        }
        else {
            Set<Long> members = new HashSet<>();
            members.add(mid);

            checkMap.put(mid, members);
        }
        //template.convertAndSend("/sub/room/" + chatDTO.getChatRoomId(), chatDTO);
    }

    @MessageMapping("/chat/message")
    public void send(ChatDTO chatDTO){

        Long chatRoomId = chatDTO.getChatRoomId();

        List<ChatDTO> chatBuffer = bufferMap.get(chatRoomId);

        /* chat -> 일단 버퍼 */
        if(chatBuffer == null){
            bufferMap.put(chatRoomId, new ArrayList<>());
            chatBuffer = bufferMap.get(chatRoomId);
        }

        chatBuffer.add(chatDTO);

        /* Persist Chat */
        /* issue : buffer에 채팅을 임시저장, 만일 서버가 예기치 않게 종료된다면, 채팅 내역이 소멸한다. 대책? */
        if(chatBuffer.size() >= 10) {
            chatService.write(chatBuffer);
            chatBuffer.clear();
        }

        chatDTO.setRegDate(LocalDateTime.now());

        /* push notification to RabbitMQ */
        notificationService.sendChatNotification(chatDTO);
        System.out.println("chatDTO = " + chatDTO);
        /* Send to other subcribers */
        template.convertAndSend("/sub/room/" + chatDTO.getChatRoomId(), chatDTO);
    }

    @MessageMapping("/chat/exit")
    public void exit(ChatDTO chatDTO){

        Long mid = chatDTO.getMemberId();
        Long cid = chatDTO.getChatRoomId();

        Set<Long> members = checkMap.get(cid);
        System.out.println("----------------------------------------------");
        members.remove(mid);

        if(members.isEmpty()) {
            checkMap.remove(cid);
        }
        //template.convertAndSend("/sub/room/" + chatDTO.getChatRoomId(), chatDTO);
    }
}