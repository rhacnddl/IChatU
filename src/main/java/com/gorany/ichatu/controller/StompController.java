package com.gorany.ichatu.controller;

import com.gorany.ichatu.domain.Chat;
import com.gorany.ichatu.dto.ChatDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;

//@Controller
@RequiredArgsConstructor
@Log4j2
public class StompController {

    private final SimpMessagingTemplate template;

    @MessageMapping("/chat/enter")
    public void enter(ChatDTO chat){

        chat.setMessage("입장하셨습니다.");
        chat.setRegDate(LocalDateTime.now());

        template.convertAndSend("/sub/chat/room/" + chat.getChatRoomId(), chat);
    }

    @MessageMapping({"/chat/message"})
    public void send(ChatDTO chat){

        chat.setRegDate(LocalDateTime.now());

        template.convertAndSend("/sub/chat/room/" + chat.getChatRoomId(), chat);
    }
}