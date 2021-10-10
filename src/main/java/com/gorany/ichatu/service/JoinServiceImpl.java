package com.gorany.ichatu.service;

import com.gorany.ichatu.domain.ChatRoom;
import com.gorany.ichatu.domain.Join;
import com.gorany.ichatu.domain.Member;
import com.gorany.ichatu.repository.jpaRepository.JoinJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class JoinServiceImpl implements JoinService {

    private final JoinJpaRepository joinJpaRepository;

    @Override
    public Boolean checkJoinMember(Long chatRoomId, Long memberId) {

        Member requester = Member.builder().id(memberId).build();
        ChatRoom chatRoom = ChatRoom.builder().id(chatRoomId).build();

        return joinJpaRepository.findByChatRoomAndMember(chatRoom, requester).get();
    }

    @Override
    @Transactional
    public Long joinChatRoom(Long chatRoomId, Long memberId) {

        Member requester = Member.builder().id(memberId).build();
        ChatRoom chatRoom = ChatRoom.builder().id(chatRoomId).build();
        Join join = Join.builder().member(requester).chatRoom(chatRoom).build();

        return joinJpaRepository.save(join);
    }

    @Override
    @Transactional
    public Integer dropChatRoom(Long chatRoomId, Long memberId) {

        Member requester = Member.builder().id(memberId).build();
        ChatRoom chatRoom = ChatRoom.builder().id(chatRoomId).build();

        Join join = Join.builder().member(requester).chatRoom(chatRoom).build();

        return joinJpaRepository.remove(join);
    }
}
