package com.gorany.ichatu.service;

import com.gorany.ichatu.domain.ChatRoom;
import com.gorany.ichatu.domain.Join;
import com.gorany.ichatu.domain.Member;
import com.gorany.ichatu.dto.AsideChatRoomDTO;
import com.gorany.ichatu.dto.ChatRoomDTO;
import com.gorany.ichatu.repository.ChatRoomRepository;
import com.gorany.ichatu.repository.JoinRepository;
import com.gorany.ichatu.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class ChatRoomServiceImpl implements ChatRoomService {

    private final NotificationRepository notificationRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final JoinRepository joinRepository;

    @Override
    @Transactional
    public Long addRoom(ChatRoomDTO chatRoomDTO) {

        /* DTO -> Entity */
        ChatRoom chatRoom = dtoToEntity(chatRoomDTO);
        /* register */
        log.info("# ChatRoomService -> ChatRoomRepository.save(ChatRoom)");
        Long chatRoomId = chatRoomRepository.save(chatRoom);
        /* Member & ChatRoom -> Join */
        Member member = chatRoom.getMember();
        Join join = Join.createJoin(member, chatRoom);

        log.info("# ChatRoomService -> JoinRepository.save(Join)");
        Long joinId = joinRepository.save(join);

        return chatRoomId;
    }

    @Override
    @Transactional
    public Long removeRoom(Long chatRoomId) {

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).get();

        log.info("# ChatRoomService -> ChatRoomRepository.remove(ChatRoom)");

        notificationRepository.removeAllByChatRoom(chatRoom);
        joinRepository.removeAll(chatRoom);

        return chatRoomRepository.remove(chatRoom);
    }

    @Override
    public ChatRoomDTO getRoom(Long id) {

        log.info("# ChatRoomService -> ChatRoomRepository.findById(ID)");
        Optional<ChatRoom> result = chatRoomRepository.findById(id);

        if(result.isEmpty())
            return null;

        return entityToDTO(result.get());
    }

    @Override
    public List<ChatRoomDTO> getRooms() {

        //지역 설정 + 페이징
        log.info("# ChatRoomService -> ChatRoomRepository.findAll()");
        Optional<List<ChatRoom>> result = chatRoomRepository.findAll();

        if(result.isEmpty())
            return null;

        List<ChatRoomDTO> rooms = result.get().stream().map(this::entityToDTO).collect(Collectors.toList());

        return rooms;
    }

    @Override
    public List<AsideChatRoomDTO> getRoomsOnAside(Long memberId) {
        Member member = Member.builder().id(memberId).build();

        //List<ChatRoom> chatRooms = joinRepository.getAsideChatRoomsWithJoinByMember(member).get().stream().map(Join::getChatRoom).collect(Collectors.toList());
        List objList = joinRepository.getAsideChatRoomsWithJoinByMemberUsingNativeSQL(member);

        List<AsideChatRoomDTO> list = (List<AsideChatRoomDTO>) objList.stream().map(obj -> AsideChatRoomDTO.createDtoByObj((Object[]) obj)).collect(Collectors.toList());

        return list;
    }

    @Override
    public boolean isOwner(Long chatRoomId, Long memberId) {

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).get();

        return chatRoom.getMember().getId().equals(memberId);
    }
}
