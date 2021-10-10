package com.gorany.ichatu.service;

import static org.assertj.core.api.Assertions.*;

import com.gorany.ichatu.domain.ChatRoom;
import com.gorany.ichatu.domain.Member;
import com.gorany.ichatu.repository.jpaRepository.ChatRoomJpaRepository;
import com.gorany.ichatu.repository.jpaRepository.MemberJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
class JoinServiceTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;
    @Autowired
    ChatRoomJpaRepository chatRoomJpaRepository;
    @Autowired JoinService joinService;

    @Test
    @DisplayName("유저 A가 채팅방 B 대상으로 가입 테스트")
    void joinChatRoom() {

        //given
        Member requester = Member.builder().nickname("User A").build();
        ChatRoom target = ChatRoom.builder().name("ChatRoom B").build();

        Long memberId = memberJpaRepository.save(requester);
        Long chatRoomId = chatRoomJpaRepository.save(target);

        //when
        Long joinId = joinService.joinChatRoom(chatRoomId, memberId);

        //then
        assertThat(joinId).isNotNull().isGreaterThan(0L);
    }

    @Test
    @DisplayName("유저 A가 채팅방 B 대상으로 나가기 테스트")
    void dropChatRoom() {

        //given
        Member requester = Member.builder().nickname("User A").build();
        ChatRoom target = ChatRoom.builder().name("ChatRoom B").build();

        Long memberId = memberJpaRepository.save(requester);
        Long chatRoomId = chatRoomJpaRepository.save(target);

        Long joinId = joinService.joinChatRoom(chatRoomId, memberId);

        //when
        joinService.dropChatRoom(chatRoomId, memberId);

        //then
        Boolean result = joinService.checkJoinMember(chatRoomId, memberId);
        assertThat(result).isFalse();
    }

}