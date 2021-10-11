package com.gorany.ichatu.service;

import com.gorany.ichatu.domain.Chat;
import com.gorany.ichatu.domain.ChatRoom;
import com.gorany.ichatu.domain.Member;
import com.gorany.ichatu.dto.ChatDTO;
import com.gorany.ichatu.repository.ChatRepository;
import com.gorany.ichatu.repository.jpaRepository.ChatJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class ChatServiceImpl implements ChatService {

    //private final ChatJpaRepository chatJpaRepository;
    private final ChatRepository chatRepository;

    @Override
    @Transactional
    public void write(List<ChatDTO> chatDTOList) {

        List<Chat> chatList = chatDTOList.stream().map(this::dtoToEntity).collect(Collectors.toList());
        log.info("# ChatService -> ChatRepository.saveAll(List<ChatDTO>)");
        chatRepository.saveAll(chatList);
    }

    @Override
    @Deprecated
    public List<ChatDTO> getChats(Long chatRoomId, Long memberId, Integer page) {

        ChatRoom chatRoom = ChatRoom.builder().id(chatRoomId).build();
        Member requester = Member.builder().id(memberId).build();
        Pageable pageable = PageRequest.of(page - 1, 20, Sort.by(Sort.Direction.DESC, "id"));

        log.info("# ChatService -> ChatRepository.findAllByChatRoom(ChatRoom, Member, Integer)");
        List<ChatDTO> chatDTOList = chatRepository.findAllByChatRoomAndMember(chatRoom, requester, pageable).stream().map(this::entityToDTO).collect(Collectors.toList());

        return chatDTOList;
    }

    @Override
    public Slice<ChatDTO> getChatsV2(Long chatRoomId, Long memberId, Integer page) {
        ChatRoom chatRoom = ChatRoom.builder().id(chatRoomId).build();
        Member requester = Member.builder().id(memberId).build();
        Pageable pageable = PageRequest.of(page - 1, 20, Sort.by(Sort.Direction.DESC, "id"));

        log.info("# ChatService -> getChatV2(ChatRoom, Member, Integer)");

        return chatRepository.findAllByChatRoomAndMember(chatRoom, requester, pageable).map(this::entityToDTO);
    }
}
