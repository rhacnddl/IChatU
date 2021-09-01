//package com.gorany.ichatu.controller;
//
//import com.gorany.ichatu.dto.ChatDTO;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.amqp.rabbit.annotation.Queue;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.messaging.handler.annotation.DestinationVariable;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Controller;
//
//import java.time.LocalDateTime;
//
////@Controller
//@RequiredArgsConstructor
//@Log4j2
//public class StompRabbitController {
//
//    private final RabbitTemplate template;
//
//    private final static String CHAT_EXCHANGE_NAME = "chat.exchange";
//    private final static String CHAT_QUEUE_NAME = "chat.queue";
//
//    @MessageMapping("chat.enter.{chatRoomId}")
//    public void enter(ChatDTO chat, @DestinationVariable String chatRoomId){
//
//        chat.setMessage("입장하셨습니다.");
//        chat.setRegDate(LocalDateTime.now());
//
//        //template.convertAndSend(CHAT_EXCHANGE_NAME, "room." + chatRoomId, chat); // exchange
//        //template.convertAndSend("room." + chatRoomId, chat); //queue
//        template.convertAndSend("amq.topic", "room." + chatRoomId, chat); //topic
//    }
//
//    @MessageMapping("chat.message.{chatRoomId}")
//    public void send(ChatDTO chat, @DestinationVariable String chatRoomId){
//
//        chat.setRegDate(LocalDateTime.now());
//
//        //template.convertAndSend(CHAT_EXCHANGE_NAME, "room." + chatRoomId, chat);
//        //template.convertAndSend("room." + chatRoomId, chat);
//        template.convertAndSend("amq.topic", "room." + chatRoomId, chat);
//    }
//
//    @RabbitListener(queues = CHAT_QUEUE_NAME)
//    public void receive(ChatDTO chat){
//
//        System.out.println("received : " + chat.getMessage());
//    }
//}