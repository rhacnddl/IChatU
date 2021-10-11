package com.gorany.ichatu.service;

import com.gorany.ichatu.domain.Chat;
import com.gorany.ichatu.domain.ChatRoom;
import com.gorany.ichatu.domain.Member;
import com.gorany.ichatu.domain.Profile;
import com.gorany.ichatu.dto.ChatDTO;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface ChatService {

    /* 채팅내역 저장 */
    void write(List<ChatDTO> chatDTOList);
    /**
     * 특정 채팅방의 채팅내역 조회 V1
     * @deprecated
     * @param chatRoomId 채팅방 ID
     * @param memberId 요청자 ID
     * @param page 조회할 페이지
     * @return List<ChatDTO> 채팅 내역
     * */
    @Deprecated
    List<ChatDTO> getChats(Long chatRoomId, Long memberId, Integer page);
    /**
     * 특정 채팅방의 채팅내역 조회 V2
     * @param chatRoomId 채팅방 ID
     * @param memberId 요청자 ID
     * @param page 조회할 페이지
     * @return List<ChatDTO> 채팅 내역
     * */
    Slice<ChatDTO> getChatsV2(Long chatRoomId, Long memberId, Integer page);

    default ChatDTO entityToDTO(Chat chat){

        Member member = chat.getMember();
        Profile profile = member.getProfile();
        ChatRoom chatRoom = chat.getChatRoom();

        return ChatDTO.builder()
                .id(chat.getId())
                .content(chat.getContent())
                .memberId(member.getId())
                .nickname(member.getNickname())
                .profileId(profile != null ? profile.getId() : null)
                .profileName(profile != null ? profile.getName() : null)
                .profilePath(profile != null ? profile.getPath() : null)
                .chatRoomId(chatRoom.getId())
                //.chatRoomName(chatRoom.getName())
                .regDate(chat.getRegDate())
                .build();
    }

    default Chat dtoToEntity(ChatDTO dto){

        ChatRoom chatRoom = ChatRoom.builder().id(dto.getChatRoomId()).build();
        Member member = Member.builder().id(dto.getMemberId()).build();

        return Chat.builder()
                .id(dto.getId())
                .chatRoom(chatRoom)
                .content(dto.getContent())
                .member(member)
                .build();
    }
}
