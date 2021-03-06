package com.gorany.ichatu.repository;

import com.gorany.ichatu.domain.ChatRoom;
import com.gorany.ichatu.domain.Member;
import com.gorany.ichatu.domain.Profile;
import com.gorany.ichatu.repository.jpaRepository.ChatRoomJpaRepository;
import com.gorany.ichatu.repository.jpaRepository.MemberJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class ChatRoomJpaRepositoryTest {

    @Autowired
    ChatRoomJpaRepository chatRoomJpaRepository;
    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    @DisplayName("채팅방 목록 조회 테스트")
    void findAll() {

        //given
        List<Profile> profiles = new ArrayList<>();
        IntStream.range(0, 50).forEach(i -> {
            profiles.add(Profile.builder().id(UUID.randomUUID().toString())
                    .name(i + "번째 프로필")
                    .path(i + "번째 경로").build());
        });

        List<Member> members = new ArrayList<>();
        IntStream.range(0, 50).forEach(i -> {
            members.add(Member.builder().profile(profiles.get(i)).nickname(i + "번째 멤버").email(i + "번째 이메일").build());
        });

        members.stream().forEach(memberJpaRepository::save);

        IntStream.range(0, 50).forEach(i -> {
            Member member = members.get(i);

            chatRoomJpaRepository.save(ChatRoom.builder().name(i + "번째 채팅방").member(member).build());
        });


        System.out.println("------------given-------------");
        //when
        List<ChatRoom> chatRooms = chatRoomJpaRepository.findAll().get();

        System.out.println("------------when---------------");
        //then
        chatRooms.forEach(c -> {
            System.out.println(c + ", " + c.getMember());
        });
        assertThat(chatRooms.size()).isEqualTo(50);
    }
}