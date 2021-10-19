package com.gorany.ichatu.service;

import com.gorany.ichatu.domain.ChatRoom;
import com.gorany.ichatu.domain.Join;
import com.gorany.ichatu.domain.Member;
import com.gorany.ichatu.domain.Region;
import com.gorany.ichatu.dto.AsideChatRoomDTO;
import com.gorany.ichatu.dto.ChatRoomDTO;
import com.gorany.ichatu.dto.ChatRoomMemberDTO;
import com.gorany.ichatu.repository.ChatRoomRepository;
import com.gorany.ichatu.repository.NotificationRepository;
import com.gorany.ichatu.repository.jpaRepository.ChatRoomJpaRepository;
import com.gorany.ichatu.repository.JoinRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
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
    //private final ChatRoomJpaRepository chatRoomJpaRepository;
    private final ChatRoomRepository chatRoomRepository;
    //private final JoinJpaRepository joinJpaRepository;
    private final JoinRepository joinRepository;

    @Override
    @Transactional
    public Long addRoom(ChatRoomDTO chatRoomDTO) {

        /* DTO -> Entity */
        ChatRoom chatRoom = dtoToEntity(chatRoomDTO);
        /* register */
        Long chatRoomId = chatRoomRepository.save(chatRoom).getId();
        /* Member & ChatRoom -> Join */
        Member member = chatRoom.getMember();
        Join join = Join.createJoin(member, chatRoom);

        Long joinId = joinRepository.save(join).getId();

        return chatRoomId;
    }

    @Override
    @Transactional
    public void removeRoom(Long chatRoomId) {

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new NoResultException("엔티티가 없습니다."));

        notificationRepository.removeAllByChatRoom(chatRoom.getId());
        joinRepository.removeAllByChatRoom(chatRoom);

        chatRoomRepository.delete(chatRoom);
    }

    @Override
    public ChatRoomDTO getRoom(Long id) {

        Optional<ChatRoom> result = chatRoomRepository.findById(id);

        if(result.isEmpty())
            return null;

        return entityToDTO(result.get());
    }

    @Override
    public Slice<ChatRoomDTO> getRooms(int page, Long regionId) {

        //지역 설정 + 페이징
        Region region = Region.builder().id(regionId).build();
        Pageable pageable = PageRequest.of(page - 1, 20, Sort.Direction.DESC, "id");

        return chatRoomRepository.findAllBy(pageable, region).map(this::entityToDTO);
    }

    @Override
    public List<AsideChatRoomDTO> getRoomsOnAside(Long memberId) {
        Member member = Member.builder().id(memberId).build();

        //List<ChatRoom> chatRooms = joinRepository.getAsideChatRoomsWithJoinByMember(member).get().stream().map(Join::getChatRoom).collect(Collectors.toList());
        List<Object[]> objList = joinRepository.getAsideChatRoomsWithJoinByMemberUsingNativeSQL(member);

        return objList.stream().map(AsideChatRoomDTO::createDtoByObj).collect(Collectors.toList());
    }

    @Override
    public boolean isOwner(Long chatRoomId, Long memberId) {

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new NoResultException("엔티티가 없습니다."));

        return chatRoom.getMember().getId().equals(memberId);
    }

    @Override
    public List<ChatRoomMemberDTO> getMembersOnChatRoom(Long chatRoomId) {

        ChatRoom chatRoom = ChatRoom.builder().id(chatRoomId).build();

        List<Join> joins = joinRepository.getMembersAndProfile(chatRoom);

        return joins.stream().map(ChatRoomMemberDTO::createChatRoomMemberDTO).collect(Collectors.toList());
    }
}
