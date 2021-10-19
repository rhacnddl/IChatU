package com.gorany.ichatu.repository.springDataJpa;

import com.gorany.ichatu.domain.*;
import com.gorany.ichatu.repository.ChatRoomRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
public class ChatRoomRepositoryTests {

    @Autowired ChatRoomRepository repository;
    @Autowired EntityManager em;

    @Test
    @DisplayName("채팅방 목록 조회 테스트")
    public void findAllByTest () throws Exception{

        /*
        * 시나리오
        * 멤버 1
        * 채팅방 27개
        * 조인 27개
        * */
        //given
        Region region = createRegion("Seoul"); em.persist(region);
        Member member = createMember("owner", "12345"); em.persist(member);

        List<ChatRoom> roomList = new ArrayList<>();
        for(int i=0; i<27; i++)
            roomList.add(createRoom(member, region, "No." + i + " room"));

        repository.saveAll(roomList);

        em.flush();
        em.clear();

        Pageable pageable = PageRequest.of(1, 10, Sort.by(Sort.Direction.DESC, "id"));
        //when
        Slice<ChatRoom> slice = repository.findAllBy(pageable, region);

        //then
        List<ChatRoom> list = slice.getContent();
        list.forEach(System.out::println);
        assertThat(slice.getSize()).isEqualTo(10);
        assertThat(list.size()).isEqualTo(10);
        assertThat(list.get(0).getId()).isGreaterThan(list.get(1).getId());
    }

    private Region createRegion(String name){
        return Region.builder().name(name).build();
    }
    private Member createMember(String nickname, String password){
        return Member.builder()
                .nickname(nickname)
                .password(password)
                .available(true)
                .role(Role.USER)
                .build();
    }
    private Profile createProfile(String name){
        return Profile.builder()
                .name(name)
                .path("random path")
                .id(UUID.randomUUID().toString())
                .build();
    }
    private Chat createChat(ChatRoom room, Member member, String content){
        return Chat.builder()
                .chatRoom(room)
                .content(content)
                .member(member)
                .build();
    }
    private ChatRoom createRoom(Member member, Region region, String name){
        return ChatRoom.builder()
                .name(name)
                .member(member)
                .region(region)
                .build();
    }
    private Join createJoin(ChatRoom room, Member member){
        return Join.builder()
                .member(member)
                .chatRoom(room)
                .build();
    }
    private Notification createNotification(Long targetId, Member sender, Member receiver){
        return Notification.builder()
                .confirm('0')
                .type(NotificationType.CHAT)
                .sender(sender)
                .receiver(receiver)
                .targetId(targetId)
                .build();
    }
}
