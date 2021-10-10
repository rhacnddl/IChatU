package com.gorany.ichatu.repository;

import com.gorany.ichatu.domain.Chat;
import com.gorany.ichatu.domain.ChatRoom;
import com.gorany.ichatu.domain.Join;
import com.gorany.ichatu.domain.Member;
import com.gorany.ichatu.repository.jpaRepository.ChatJpaRepository;
import com.gorany.ichatu.repository.jpaRepository.ChatRoomJpaRepository;
import com.gorany.ichatu.repository.jpaRepository.JoinJpaRepository;
import com.gorany.ichatu.repository.jpaRepository.MemberJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class ChatJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;
    @Autowired
    ChatRoomJpaRepository chatRoomJpaRepository;
    @Autowired
    JoinJpaRepository joinJpaRepository;
    @Autowired
    ChatJpaRepository chatJpaRepository;

    @Test
    void saveAll() {
    }

    @Test
    @DisplayName("특정 채팅방의 채팅 LOAD with Paging")
    void findAllByChatRoomAndMember() {

        //given
        int page = 1;
        Member member = Member.builder().nickname("test User").build();
        Long memberId = memberJpaRepository.save(member);

        ChatRoom chatRoom = ChatRoom.builder().name("test Room").member(member).build();
        Long chatRoomId = chatRoomJpaRepository.save(chatRoom);

        Join join = Join.builder().chatRoom(chatRoom).member(member).build();
        Long joinId = joinJpaRepository.save(join);

        ArrayList<Chat> chats = new ArrayList<>();

        IntStream.rangeClosed(0, 40).forEach(i -> {
            Chat chat = Chat.builder().content("TEST CHAT : " + i).member(member).chatRoom(chatRoom).build();
            chats.add(chat);
        });

        chatJpaRepository.saveAll(chats);
        System.out.println("----- given ------");
        //when

        List<Chat> findList = chatJpaRepository.findAllByChatRoomAndMember(chatRoom, member, page);
        System.out.println("----- when ------");
        //then
        findList.forEach(System.out::println);

        assertThat(findList.size()).isEqualTo(20);
        System.out.println("----- then ------");
    }
}