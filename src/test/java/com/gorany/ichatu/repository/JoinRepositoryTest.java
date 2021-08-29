package com.gorany.ichatu.repository;

import com.gorany.ichatu.domain.ChatRoom;
import com.gorany.ichatu.domain.Join;
import com.gorany.ichatu.domain.Member;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JoinRepositoryTest {

    @Autowired JoinRepository joinRepository;
    @Autowired ChatRoomRepository chatRoomRepository;
    @Autowired MemberRepository memberRepository;

    @Test
    @DisplayName("사용자가 특정 채팅방에 가입했는지 여부 테스트")
    @Transactional
    void findByChatRoomAndMember() {

        //given
        Member member = Member.builder().nickname("test instance").build();
        ChatRoom chatRoom = ChatRoom.builder().name("test ChatRoom").build();

        Long save = memberRepository.save(member);
        Long roomSave = chatRoomRepository.save(chatRoom);

        Long joinSave = joinRepository.save(Join.builder().chatRoom(chatRoom).member(member).build());

        //when
        Optional<Boolean> find = joinRepository.findByChatRoomAndMember(chatRoom, member);

        //then
        assertThat(find.get()).isEqualTo(Boolean.TRUE);
    }
}