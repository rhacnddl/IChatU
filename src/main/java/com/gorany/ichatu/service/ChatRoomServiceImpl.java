package com.gorany.ichatu.service;

import com.gorany.ichatu.domain.ChatRoom;
import com.gorany.ichatu.domain.Join;
import com.gorany.ichatu.domain.Member;
import com.gorany.ichatu.dto.AsideChatRoomDTO;
import com.gorany.ichatu.dto.ChatRoomDTO;
import com.gorany.ichatu.dto.ChatRoomMemberDTO;
import com.gorany.ichatu.repository.NotificationRepository;
import com.gorany.ichatu.repository.jpaRepository.ChatRoomJpaRepository;
import com.gorany.ichatu.repository.jpaRepository.JoinJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class ChatRoomServiceImpl implements ChatRoomService {

    //private final NotificationJpaRepository notificationJpaRepository;
    private final NotificationRepository notificationRepository;
    private final ChatRoomJpaRepository chatRoomJpaRepository;
    private final JoinJpaRepository joinJpaRepository;

    @Override
    @Transactional
    public Long addRoom(ChatRoomDTO chatRoomDTO) {

        /* DTO -> Entity */
        ChatRoom chatRoom = dtoToEntity(chatRoomDTO);
        /* register */
        log.info("# ChatRoomService -> ChatRoomRepository.save(ChatRoom)");
        Long chatRoomId = chatRoomJpaRepository.save(chatRoom);
        /* Member & ChatRoom -> Join */
        Member member = chatRoom.getMember();
        Join join = Join.createJoin(member, chatRoom);

        log.info("# ChatRoomService -> JoinRepository.save(Join)");
        Long joinId = joinJpaRepository.save(join);

        return chatRoomId;
    }

    @Override
    @Transactional
    public Long removeRoom(Long chatRoomId) {

        ChatRoom chatRoom = chatRoomJpaRepository.findById(chatRoomId).get();

        log.info("# ChatRoomService -> ChatRoomRepository.remove(ChatRoom)");

        notificationRepository.removeAllByChatRoom(chatRoom.getId());
        joinJpaRepository.removeAll(chatRoom);

        return chatRoomJpaRepository.remove(chatRoom);
    }

    @Override
    public ChatRoomDTO getRoom(Long id) {

        log.info("# ChatRoomService -> ChatRoomRepository.findById(ID)");
        Optional<ChatRoom> result = chatRoomJpaRepository.findById(id);

        if(result.isEmpty())
            return null;

        return entityToDTO(result.get());
    }

    @Override
    public List<ChatRoomDTO> getRooms() {

        //지역 설정 + 페이징
        log.info("# ChatRoomService -> ChatRoomRepository.findAll()");
        Optional<List<ChatRoom>> result = chatRoomJpaRepository.findAll();

        if(result.isEmpty())
            return null;

        List<ChatRoomDTO> rooms = result.get().stream().map(this::entityToDTO).collect(Collectors.toList());

        return rooms;
    }

    @Override
    public List<AsideChatRoomDTO> getRoomsOnAside(Long memberId) {
        Member member = Member.builder().id(memberId).build();

        //List<ChatRoom> chatRooms = joinRepository.getAsideChatRoomsWithJoinByMember(member).get().stream().map(Join::getChatRoom).collect(Collectors.toList());
        List objList = joinJpaRepository.getAsideChatRoomsWithJoinByMemberUsingNativeSQL(member);

        List<AsideChatRoomDTO> list = (List<AsideChatRoomDTO>) objList.stream().map(obj -> AsideChatRoomDTO.createDtoByObj((Object[]) obj)).collect(Collectors.toList());

        return list;
    }

    @Override
    public boolean isOwner(Long chatRoomId, Long memberId) {

        ChatRoom chatRoom = chatRoomJpaRepository.findById(chatRoomId).get();

        return chatRoom.getMember().getId().equals(memberId);
    }

    @Override
    public List<ChatRoomMemberDTO> getMembersOnChatRoom(Long chatRoomId) {

        ChatRoom chatRoom = ChatRoom.builder().id(chatRoomId).build();

        List<Join> joins = joinJpaRepository.getMembersAndProfile(chatRoom).get();

        return joins.stream().map(ChatRoomMemberDTO::createChatRoomMemberDTO).collect(Collectors.toList());
    }
}
