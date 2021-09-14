package com.gorany.ichatu.service;

import com.gorany.ichatu.domain.*;
import com.gorany.ichatu.dto.ChatDTO;
import com.gorany.ichatu.dto.NotificationDTO;
import com.gorany.ichatu.storage.TokenStorage;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

public interface NotificationService {

    /* 미확인 알림 개수 반환 */
    Long getNotConfirmedCount(Long memberId);

    /* 채팅 -> 알림 전송 */
    void sendChatNotification(ChatDTO chatDTO);
    List<NotificationDTO> saveAndTransform(List<Long> memberIdList, ChatDTO chatDTO);

    /* 알림 모두 확인 : confirm column (0 -> 1) */
    Integer checkNotifications(Long memberId);

    /* 알림 단건 확인 */
    Integer checkNotification(Long notificationId);

    /* 알림 모두 제거 */
    Integer removeNotifications(Long memberId);

    /* 알림 목록 조회 */
    List<NotificationDTO> getNotificationList(Long memberId, Integer page);

    /* 댓글 -> 알림 전송 */
    void sendCommentNotification();

    /* 채팅방 입장 -> 알림 확인 처리 */
    Integer updateNotificationsByChatRoomAndMember(Long chatRoomId, Long memberId);

    /* 댓글 -> 알림 */
    default NotificationDTO commentToNotification(){
        return null;
    }
    
    /* 채팅 -> 알림 */
    default Notification chatToNotification(ChatDTO chatDTO, Member sender, Member receiver){

//        Profile profile = Profile.builder().id(chatDTO.getProfileId()).name(chatDTO.getProfileName()).path(chatDTO.getProfilePath()).build();
//        sender.changeProfile(profile);

        return Notification.builder()
                .type(NotificationType.CHAT)
                .targetId(chatDTO.getChatRoomId())
                .sender(sender)
                .receiver(receiver)
                .confirm('0') //0 : 미확인, 1 : 확인
                .build();
    }

    /* Notification Entity -> Notification DTO */
    default NotificationDTO entityToDTO(Notification entity, Member sender, @Nullable Profile profile){
        return NotificationDTO.builder()
                .id(entity.getId())
                .senderId(sender.getId())
                .senderNickname(sender.getNickname())
                .receiverId(entity.getReceiver().getId())
                .targetId(entity.getTargetId())
                .type(entity.getType())
                .regDate(entity.getRegDate())
                .confirm(entity.getConfirm())
                .senderProfileId(profile != null ? profile.getId() : null)
                .senderProfileName(profile != null ? profile.getName() : null)
                .senderProfilePath(profile != null ? profile.getPath() : null)
                .build();
    }

    /* Notification DTO -> Notification Entity */
    default Notification dtoToEntity(NotificationDTO dto){

        Member sender = Member.builder().id(dto.getSenderId()).build();
        Member receiver = Member.builder().id(dto.getReceiverId()).build();

        return Notification.builder()
                .targetId(dto.getTargetId())
                .type(dto.getType())
                .receiver(receiver)
                .sender(sender)
                .id(dto.getId())
                .build();
    }

}