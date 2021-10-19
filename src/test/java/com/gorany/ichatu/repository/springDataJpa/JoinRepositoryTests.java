package com.gorany.ichatu.repository.springDataJpa;

import com.gorany.ichatu.domain.*;
import com.gorany.ichatu.dto.AsideChatRoomDTO;
import com.gorany.ichatu.repository.JoinRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
public class JoinRepositoryTests {

    @Autowired JoinRepository repository;
    @Autowired EntityManager em;

    /**
     * 지역 1개 - Seoul
     * 멤버 5개 - A,B,C,D,E
     * 프로필 5개 - a,b,c,d,e
     * 채팅방 2개 - room1, room2 by A
     * 채팅 각 20개 - chat 1 ~ chat 20 by A
     * Join 7개
     * room1 -> {A, B, C, E}
     * room2 -> {A, D, E}
     *
     * D가 조회하면 room2만 나와야한다.
     * */
    @Test
    @DisplayName("Aside 화면에 맞춘 쿼리메서드 테스트")
    public void getAsideChatRoomsWithJoinByMemberUsingNativeSQLTest() throws Exception{
        //given
        Region region = createRegion("Seoul"); em.persist(region);

        Member A = createMember("A", "12345"); em.persist(A);
        Member B = createMember("B", "12345"); em.persist(B);
        Member C = createMember("C", "12345"); em.persist(C);
        Member D = createMember("D", "12345"); em.persist(D);
        Member E = createMember("E", "12345"); em.persist(E);

        Profile a = createProfile("a"); em.persist(a); A.changeProfile(a);
        Profile b = createProfile("b"); em.persist(b); B.changeProfile(b);
        Profile c = createProfile("c"); em.persist(c); C.changeProfile(c);
        Profile d = createProfile("d"); em.persist(d); D.changeProfile(d);
        Profile e = createProfile("e"); em.persist(e); E.changeProfile(e);

        ChatRoom room1 = createRoom(A, region, "room1"); em.persist(room1);
        ChatRoom room2 = createRoom(A, region, "room2"); em.persist(room2);

        Join j1 = createJoin(room1, A); em.persist(j1);
        Join j2 = createJoin(room1, B); em.persist(j2);
        Join j3 = createJoin(room1, C); em.persist(j3);
        Join j4 = createJoin(room1, E); em.persist(j4);

        Join j1_ = createJoin(room2, A); em.persist(j1_);
        Join j2_ = createJoin(room2, D); em.persist(j2_);
        Join j3_ = createJoin(room2, E); em.persist(j3_);

        for(int i=0; i<20; i++) {
            em.persist(createChat(room1, A, "room 1's chat " + i));
            Notification n1 = createNotification(room1.getId(), A, B); em.persist(n1);
            Notification n2 = createNotification(room1.getId(), A, C); em.persist(n2);
            Notification n3 = createNotification(room1.getId(), A, E); em.persist(n3);
        }

        for(int i=0; i<20; i++) {
            em.persist(createChat(room2, A, "room 2's chat " + i));
            Notification n1 = createNotification(room2.getId(), A, D); em.persist(n1);
            Notification n2 = createNotification(room2.getId(), A, E); em.persist(n2);
        }

        em.flush();
        em.clear();
        //when
        List<Object[]> result = repository.getAsideChatRoomsWithJoinByMemberUsingNativeSQL(D);

        //then
        List<AsideChatRoomDTO> list = result.stream().map(AsideChatRoomDTO::createDtoByObj).collect(Collectors.toList());

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0).getCnt()).isEqualTo(20);
        assertThat(list.get(0).getNickname()).isEqualTo("A");
        assertThat(list.get(0).getProfileName()).isEqualTo("a");
    }

    @Test
    @DisplayName("채팅방 삭제 시 Join 모두 삭제 테스트")
    public void removeAllByChatRoomTest() throws Exception{

        //given
        Region region = createRegion("Seoul"); em.persist(region);

        Member A = createMember("A", "12345"); em.persist(A);
        Member B = createMember("B", "12345"); em.persist(B);
        Member C = createMember("C", "12345"); em.persist(C);
        Member E = createMember("E", "12345"); em.persist(E);

        ChatRoom room1 = createRoom(A, region, "room1"); em.persist(room1);

        Join j1 = createJoin(room1, A); em.persist(j1);
        Join j2 = createJoin(room1, B); em.persist(j2);
        Join j3 = createJoin(room1, C); em.persist(j3);
        Join j4 = createJoin(room1, E); em.persist(j4);

        em.flush();
        em.clear();
        //when
        int count = repository.removeAllByChatRoom(room1);

        //then
        assertThat(count).isEqualTo(4);
    }

    @Test
    @DisplayName("A가 B에 가입했는지 여부를 숫자로 조회 테스트")
    public void getCountblarblarTest() throws Exception{

        //given
        Region region = createRegion("Seoul"); em.persist(region);

        Member A = createMember("A", "12345"); em.persist(A);
        Member B = createMember("B", "12345"); em.persist(B);
        Member C = createMember("C", "12345"); em.persist(C);
        Member E = createMember("E", "12345"); em.persist(E);
        Member D = createMember("D", "12345"); em.persist(D);

        ChatRoom room1 = createRoom(A, region, "room1"); em.persist(room1);

        Join j1 = createJoin(room1, A); em.persist(j1);
        Join j2 = createJoin(room1, B); em.persist(j2);
        Join j3 = createJoin(room1, C); em.persist(j3);
        Join j4 = createJoin(room1, E); em.persist(j4);

        em.flush();
        em.clear();
        //when
        Long count = repository.getCountByChatRoomAndMember(room1, A);

        //then
        assertThat(count).isEqualTo(1L);
    }

    @Test
    @DisplayName("채팅방에 가입한 멤버 + 프로필 조회 테스트")
    public void getMembersAndProfileTest() throws Exception{

        //given
        Region region = createRegion("Seoul"); em.persist(region);

        Member A = createMember("A", "12345"); em.persist(A);
        Member B = createMember("B", "12345"); em.persist(B);
        Member C = createMember("C", "12345"); em.persist(C);
        Member E = createMember("E", "12345"); em.persist(E);

        Profile a = createProfile("a"); em.persist(a); A.changeProfile(a);
        Profile b = createProfile("b"); em.persist(b); B.changeProfile(b);
        Profile c = createProfile("c"); em.persist(c); C.changeProfile(c);
        Profile e = createProfile("e"); em.persist(e); E.changeProfile(e);

        ChatRoom room1 = createRoom(A, region, "room1"); em.persist(room1);

        Join j1 = createJoin(room1, A); em.persist(j1);
        Join j2 = createJoin(room1, B); em.persist(j2);
        Join j3 = createJoin(room1, C); em.persist(j3);
        Join j4 = createJoin(room1, E); em.persist(j4);

        em.flush();
        em.clear();
        //when
        List<Join> list = repository.getMembersAndProfile(room1);

        //then
        assertThat(list.size()).isEqualTo(4);
        assertThat(getProfileName(list.get(0).getMember().getNickname())).isEqualTo(list.get(0).getMember().getProfile().getName());
    }

    private static final String[] NICKNAMES = {"A", "B", "C", "D", "E"};
    private static final String[] NAMES = {"a", "b", "c", "d", "e"};

    private String getProfileName(String nickname){

        for (int i=0; i<5; i++)
            if(nickname.equals(NICKNAMES[i]))
                return NAMES[i];

        return "fail";
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
