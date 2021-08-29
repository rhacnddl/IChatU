package com.gorany.ichatu.service;

import static org.assertj.core.api.Assertions.*;

import com.gorany.ichatu.domain.ChatRoom;
import com.gorany.ichatu.domain.Join;
import com.gorany.ichatu.domain.Member;
import com.gorany.ichatu.repository.ChatRoomRepository;
import com.gorany.ichatu.repository.JoinRepository;
import com.gorany.ichatu.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
class JoinServiceTest {

    @Autowired MemberRepository memberRepository;
    @Autowired ChatRoomRepository chatRoomRepository;
    @Autowired JoinService joinService;

    @Test
    void checkJoinMember() {
    }

    @Test
    @DisplayName("유저 A가 채팅방 B 대상으로 가입 테스트")
    @Rollback(true)
    void joinChatRoom() {

        //given
        Member requester = Member.builder().nickname("User A").build();
        ChatRoom target = ChatRoom.builder().name("ChatRoom B").build();

        Long memberId = memberRepository.save(requester);
        Long chatRoomId = chatRoomRepository.save(target);

        //when
        Long joinId = joinService.joinChatRoom(chatRoomId, memberId);

        //then
        assertThat(joinId).isNotNull().isGreaterThan(0L);
    }

    @Test
    @DisplayName("유저 A가 채팅방 B 대상으로 나가기 테스트")
    @Transactional
    void dropChatRoom() {

        //given
        Member requester = Member.builder().nickname("User A").build();
        ChatRoom target = ChatRoom.builder().name("ChatRoom B").build();

        Long memberId = memberRepository.save(requester);
        Long chatRoomId = chatRoomRepository.save(target);

        Long joinId = joinService.joinChatRoom(chatRoomId, memberId);

        //when
        joinService.dropChatRoom(chatRoomId, memberId);

        //then
        Boolean result = joinService.checkJoinMember(chatRoomId, memberId);
        assertThrows(Exception.class, () -> {

            assertThat(result).isTrue();
        });


        System.out.println("얘가 출력되면 안됨");
    }
}