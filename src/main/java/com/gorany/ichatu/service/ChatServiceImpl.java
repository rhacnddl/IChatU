package com.gorany.ichatu.service;

import com.gorany.ichatu.domain.Chat;
import com.gorany.ichatu.domain.ChatRoom;
import com.gorany.ichatu.domain.Member;
import com.gorany.ichatu.dto.ChatDTO;
import com.gorany.ichatu.repository.jpaRepository.ChatJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class ChatServiceImpl implements ChatService {

    private final ChatJpaRepository chatJpaRepository;

    @Override
    @Transactional
    public void write(List<ChatDTO> chatDTOList) {

        List<Chat> chatList = chatDTOList.stream().map(this::dtoToEntity).collect(Collectors.toList());
        log.info("# ChatService -> ChatRepository.saveAll(List<ChatDTO>)");
        chatJpaRepository.saveAll(chatList);
    }

    @Override
    public List<ChatDTO> getChats(Long chatRoomId, Long memberId, Integer page) {

        ChatRoom chatRoom = ChatRoom.builder().id(chatRoomId).build();
        Member requester = Member.builder().id(memberId).build();

        log.info("# ChatService -> ChatRepository.findAllByChatRoom(ChatRoom, Member, Integer)");
        List<ChatDTO> chatDTOList = chatJpaRepository.findAllByChatRoomAndMember(chatRoom, requester, page).stream().map(this::entityToDTO).collect(Collectors.toList());

        return chatDTOList;
    }
}
