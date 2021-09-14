package com.gorany.ichatu.service;

import com.gorany.ichatu.domain.ChatRoom;
import com.gorany.ichatu.domain.Member;
import com.gorany.ichatu.domain.Profile;
import com.gorany.ichatu.domain.Region;
import com.gorany.ichatu.dto.AsideChatRoomDTO;
import com.gorany.ichatu.dto.ChatRoomDTO;

import java.util.List;
import java.util.Optional;

public interface ChatRoomService {

    /* 채팅방 추가 */
    Long addRoom(ChatRoomDTO chatRoomDTO);
    /* 채팅방 삭제 */
    Long removeRoom(Long chatRoomId);
    /* 채팅방 조회(단건) */
    ChatRoomDTO getRoom(Long id);
    /* 채팅방 조회(list) */
    List<ChatRoomDTO> getRooms();
    /* A가 가입한 채팅방 목록 (ASIDE) */
    List<AsideChatRoomDTO> getRoomsOnAside(Long memberId);
    /* 채팅방 삭제시, 주인인지 유효성 검사 */
    boolean isOwner(Long chatRoomId, Long memberId);

    default ChatRoom dtoToEntity(ChatRoomDTO dto){

        Member member = Member.builder().id(dto.getMemberId()).nickname(dto.getNickname()).build();
        Region region = Region.builder().id(dto.getRegionId()).build();

        return ChatRoom.builder()
                .id(dto.getId())
                .name(dto.getName())
                .member(member)
                .region(region)
                .build();
    }

    default ChatRoomDTO entityToDTO(ChatRoom entity){

        Member member = entity.getMember();
        Profile profile = member.getProfile();
        Region region = entity.getRegion();

        return ChatRoomDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .memberId(member.getId())
                .nickname(member.getNickname())
                .profileId(profile != null? profile.getId() : null)
                .profileName(profile != null?profile.getName() : null)
                .profilePath(profile != null?profile.getPath() : null)
                .regionId(region.getId())
                .regDate(entity.getRegDate())
                .build();
    }

}
