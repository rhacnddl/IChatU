package com.gorany.ichatu.repository.springDataJpa;

import com.gorany.ichatu.domain.*;
import com.gorany.ichatu.repository.ChatRepository;
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

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Transactional
public class ChatRepositoryTests {

    @Autowired EntityManager em;
    @Autowired ChatRepository repository;

    @Test
    @DisplayName("채팅 내역 조회 테스트")
    public void findAllByMemberAndChatRoom_Test() throws Exception{
        //given
        /* region */
        Region region = Region.builder().name("Seoul").build(); em.persist(region);
        /* member */
        Member owner = createMember("owner", "12345"); em.persist(owner);
        Member joiner = createMember("joiner", "54321"); em.persist(joiner);
        /* profile */
        Profile ownerProfile = createProfile("방장");
        Profile joinerProfile = createProfile("참가자");
        owner.changeProfile(ownerProfile);
        joiner.changeProfile(joinerProfile);
        /* room */
        ChatRoom room = createRoom(owner, region, "Test Room"); em.persist(room);
        Join ownerJoin = createJoin(room, owner); em.persist(ownerJoin);

        /* chats (조회되면 안되는 것) */
        List<Chat> preChats = new ArrayList<>();
        for(int i=0; i<10; i++)
            preChats.add(createChat(room, owner, "조회되면 안댐"));

        repository.saveAll(preChats);
        em.flush();
        em.clear();

        Join joinerJoin = createJoin(room, joiner); em.persist(joinerJoin);
        /* 조회되는 것 */
        List<Chat> postChats = new ArrayList<>();
        for(int i=0; i<15; i++)
            postChats.add(createChat(room, joiner, "조회되야함"));

        repository.saveAll(postChats);
        em.flush();
        em.clear();

        int page = 0;
        int size = 20;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        //when
        Slice<Chat> slice = repository.findAllByChatRoomAndMember(room, joiner, pageable);
        List<Chat> chats = slice.getContent();
        //then
        assertThat(chats.get(0).getChatRoom()).isNotEqualTo("조회되면 안됨");
        assertThat(slice.getNumberOfElements()).isEqualTo(15);
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
}
