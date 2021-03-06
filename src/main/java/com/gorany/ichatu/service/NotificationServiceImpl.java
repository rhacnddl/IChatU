package com.gorany.ichatu.service;

import com.gorany.ichatu.domain.*;
import com.gorany.ichatu.dto.ChatDTO;
import com.gorany.ichatu.dto.NotificationDTO;
import com.gorany.ichatu.repository.NotificationRepository;
import com.gorany.ichatu.repository.JoinRepository;
import com.gorany.ichatu.storage.CheckStorage;
import com.gorany.ichatu.storage.TokenStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService{

    @Value("${exchange.name}")
    private String EXCHANGE_NAME;
    @Value("${chat.queue.name}")
    private String CHAT_QUEUE_NAME;
    @Value("${comment.queue.name}")
    private String COMMENT_QUEUE_NAME;

    private static final CheckStorage checkStorage = CheckStorage.getInstance();
    private Map<Long, Set<Long>> checkMap;

    private static final TokenStorage storage = TokenStorage.getInstance();
    private final Map<Long, String> map = storage.getMap();

    private final String CHAT_ROUTING_KEY = "chat.";
    private final String COMMENT_ROUTING_KEY = "comment.";

    private final RabbitTemplate template;
    //private final JoinJpaRepository joinJpaRepository;
    private final JoinRepository joinRepository;
    //private final NotificationJpaRepository notificationJpaRepository;
    private final NotificationRepository notificationRepository;

    @PostConstruct
    private void init(){
        this.checkMap = checkStorage.getCheckMap();
    }

    //????????? ?????? ?????? ??????
    @Override
    public Long getNotConfirmedCount(Long memberId) {
        //return notificationJpaRepository.getCount(Member.builder().id(memberId).build());
        return notificationRepository.getCount(Member.builder().id(memberId).build());
    }

    /*
        1. ChatDTO??? ?????????.
        2. ?????? ???????????? ????????? ????????? ID ????????? ?????????.
        3. ??? ???????????? ???????????? Notification Entity List??? ?????????. (?????? ?????? 1??????)
        4. NotificationList??? ??????(?????????)??????.
        5. Notification -> NotificationDTO transform
        6. Send to Rabbit
        *  */
    @Override
    @Transactional
    public void sendChatNotification(ChatDTO chatDTO) {
        /* debug */
        /*map.forEach((k, v) -> {
            System.out.println("(k, v) = " + k + ", " + v);
        });*/

        /* get all User List in ChatRoom */
        List<Long> memberIdList = joinRepository.findMemberIdsByChatRoom(ChatRoom.builder().id(chatDTO.getChatRoomId()).build());

        /* send -> Database & return DTO List*/
        List<NotificationDTO> notificationDTOList = saveAndTransform(memberIdList, chatDTO);

        /* send -> Rabbit */
        if(!notificationDTOList.isEmpty())
            template.convertAndSend(EXCHANGE_NAME, CHAT_ROUTING_KEY + chatDTO.getChatRoomId(), notificationDTOList);
    }

    @Override
    @Transactional
    public List<NotificationDTO> saveAndTransform(List<Long> memberIdList, ChatDTO chatDTO) {
        /* define profile */
        Profile profile = Profile.builder().id(chatDTO.getProfileId()).name(chatDTO.getProfileName()).path(chatDTO.getProfilePath()).build();
        /* define sender */
        Member sender = Member.builder()
                .id(chatDTO.getMemberId())
                .nickname(chatDTO.getNickname())
                .build();
        /* define receiver and change to Notification (????????? == ????????? ??? ??? ??????) OR (???????????? ????????? ?????? ??????) */
        Set<Long> members = checkMap.get(chatDTO.getChatRoomId());
        //members.forEach(System.out::println);

        List<Notification> notificationList = memberIdList.stream()
                .filter(id -> !members.contains(id))
//                    .filter(id -> id != sender.getId() || !members.contains(id))
                    .map(id -> {
                        Member receiver = Member.builder().id(id).build();

                        return chatToNotification(chatDTO, sender, receiver);
                    }).collect(Collectors.toList());

        /* save Notification */
        notificationRepository.saveAll(notificationList);

        /* ?????? ????????? ??????????????? ??????????????? ?????? token ?????? ????????? ????????? */
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
    @Transactional
    public Integer updateNotificationsByChatRoomAndMember(Long chatRoomId, Long memberId) {

        Member receiver = Member.builder().id(memberId).build();

        //return notificationJpaRepository.updateAllByChatRoomAndMember(chatRoomId, receiver);
        return notificationRepository.updateAllByChatRoomAndMember(receiver, chatRoomId);
    }

    @Override
    @Transactional
    public void checkOne(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new IllegalArgumentException("?????? ????????? ????????????."));

        notification.confirm();
    }

    @Override
    public List<NotificationDTO> getNotificationList(Long memberId, Integer page) {

        /* Long -> Member */
        Member receiver = Member.builder().id(memberId).build();
        /* get Entities */
        //List<Notification> entities = notificationJpaRepository.findAllByMemberId(receiver, page).get();
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Slice<Notification> slices = notificationRepository.findAllByMemberId(receiver, pageable);
        List<Notification> entities = slices.getContent();
        /* convert entities -> DTOs */
        List<NotificationDTO> dtos = entities.stream().map(en -> {

            Member sender = en.getSender();
            Profile profile = sender.getProfile();

            return entityToDTO(en, sender, profile);
        }).collect(Collectors.toList());

        return dtos;
    }

    @Override
    @Transactional
    public Integer checkNotifications(Long memberId) {

        Member receiver = Member.builder().id(memberId).build();

        //return notificationJpaRepository.updateAll(receiver);
        return notificationRepository.updateAll(receiver);
    }

    @Override
    @Transactional
    public void removeAll(Long memberId) {

        Member receiver = Member.builder().id(memberId).build();
        notificationRepository.deleteAll(receiver);
    }

    /* Deprecated */
    @Override
    @Transactional
    @Deprecated
    public Integer removeNotifications(Long memberId) {

        Member receiver = Member.builder().id(memberId).build();

        //return notificationJpaRepository.deleteAll(receiver);
        return null;
    }
    @Override
    @Transactional
    @Deprecated
    public Integer checkNotification(Long notificationId) {
        //return notificationJpaRepository.update(notificationId);
        return null;
    }
}
