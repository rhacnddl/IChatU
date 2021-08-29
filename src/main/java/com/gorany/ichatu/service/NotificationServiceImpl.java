package com.gorany.ichatu.service;

import com.gorany.ichatu.domain.*;
import com.gorany.ichatu.dto.ChatDTO;
import com.gorany.ichatu.dto.NotificationDTO;
import com.gorany.ichatu.repository.JoinRepository;
import com.gorany.ichatu.repository.NotificationRepository;
import com.gorany.ichatu.storage.TokenStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{

    @Value("${exchange.name}")
    private String EXCHANGE_NAME;
    @Value("${chat.queue.name}")
    private String CHAT_QUEUE_NAME;
    @Value("${comment.queue.name}")
    private String COMMENT_QUEUE_NAME;

    private static final TokenStorage storage = TokenStorage.getInstance();
    private final Map<Long, String> map = storage.getMap();

    private final String CHAT_ROUTING_KEY = "chat.";
    private final String COMMENT_ROUTING_KEY = "comment.";

    private final RabbitTemplate template;
    private final JoinRepository joinRepository;
    private final NotificationRepository notificationRepository;

    //미확인 알림 개수 확인
    @Override
    public Long getNotConfirmedCount(Long memberId) {
        return notificationRepository.getCount(Member.builder().id(memberId).build());
    }

    /*
        1. ChatDTO를 받는다.
        2. 해당 채팅방에 가입한 사람의 ID 목록을 뽑는다.
        3. 그 사람들을 대상으로 Notification Entity List를 만든다. (두당 알림 1개씩)
        4. NotificationList를 저장(영속화)한다.
        5. Notification -> NotificationDTO transform
        6. Send to Rabbit
        *  */
    @Override
    public void sendChatNotification(ChatDTO chatDTO) {
        /* debug */
        map.forEach((k, v) -> {
            System.out.println("(k, v) = " + k + ", " + v);
        });

        /* get all User List in ChatRoom */
        List<Long> memberIdList = joinRepository.findMemberIdsByChatRoom(ChatRoom.builder().id(chatDTO.getChatRoomId()).build()).get();

        /* send -> Database & return DTO List*/
        List<NotificationDTO> notificationDTOList = saveAndTransform(memberIdList, chatDTO);

        /* send -> Rabbit */
        if(!notificationDTOList.isEmpty())
            template.convertAndSend(EXCHANGE_NAME, CHAT_ROUTING_KEY + chatDTO.getChatRoomId(), notificationDTOList);
    }

    @Override
    public List<NotificationDTO> saveAndTransform(List<Long> memberIdList, ChatDTO chatDTO) {
        /* define profile */
        Profile profile = Profile.builder().id(chatDTO.getProfileId()).name(chatDTO.getProfileName()).path(chatDTO.getProfilePath()).build();
        /* define sender */
        Member sender = Member.builder()
                .id(chatDTO.getMemberId())
                .nickname(chatDTO.getNickname())
                .build();

        /* define receiver and change to Notification (발신자 == 수신자 인 것 제외) */
        List<Notification> notificationList = memberIdList.stream()
                .filter(id -> id != sender.getId())
                .map(id -> {
                    Member receiver = Member.builder().id(id).build();

                    return chatToNotification(chatDTO, sender, receiver);
                }).collect(Collectors.toList());

        /* save Notification */
        notificationRepository.saveAll(notificationList);

        /* 현재 접속한 사람한테만 알림보내기 위해 token 있는 사람만 걸러냄 */
        return notificationList.stream()
                .filter(nt -> map.containsKey(nt.getReceiver().getId()))
                .map(nt -> {
                    NotificationDTO notificationDTO = entityToDTO(nt, sender, profile);
                    notificationDTO.setToken(map.get(notificationDTO.getReceiverId()));

                    return notificationDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void sendCommentNotification() {

    }

    @Override
    public Integer checkNotification(Long notificationId) {
        return notificationRepository.update(notificationId);
    }

    @Override
    public List<NotificationDTO> getNotificationList(Long memberId, Integer page) {

        /* Long -> Member */
        Member receiver = Member.builder().id(memberId).build();
        /* get Entities */
        List<Notification> entities = notificationRepository.findAllByMemberId(receiver, page).get();
        /* convert entities -> DTOs */
        List<NotificationDTO> dtos = entities.stream().map(en -> {

            Member sender = en.getSender();
            Profile profile = sender.getProfile();

            return entityToDTO(en, sender, profile);
        }).collect(Collectors.toList());

        dtos.forEach(System.out::println);

        return dtos;
    }

    @Override
    public Integer checkNotifications(Long memberId) {

        Member receiver = Member.builder().id(memberId).build();

        return notificationRepository.updateAll(receiver);
    }

    @Override
    public Integer removeNotifications(Long memberId) {

        Member receiver = Member.builder().id(memberId).build();

        return notificationRepository.deleteAll(receiver);
    }
}
