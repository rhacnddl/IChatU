package com.gorany.ichatu.repository;

import com.gorany.ichatu.domain.*;

import static org.assertj.core.api.Assertions.*;

import com.gorany.ichatu.dto.AsideChatRoomDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class JoinRepositoryTest {

    @Autowired JoinRepository joinRepository;
    @Autowired EntityManager em;

    @Test
    @DisplayName("사용자가 특정 채팅방에 가입했는지 여부 테스트")
    void findByChatRoomAndMember() {

        //given
        Member member = Member.builder().nickname("test instance").build();
        Member creator = Member.builder().nickname("room creator").build();
        em.persist(member);
        em.persist(creator);

        ChatRoom chatRoom = ChatRoom.builder().member(creator).name("test ChatRoom").build();
        em.persist(chatRoom);

        Long joinSave = joinRepository.save(Join.builder().chatRoom(chatRoom).member(member).build());

        //when
        Boolean result = joinRepository.findByChatRoomAndMember(chatRoom, member).get();

        //then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("어사이드에 보일 채팅방 목록을 가져오는 테스트 (중요)")
    void 어사이드_채팅방_목록테스트() {

        //given
        Region region = Region.builder().name("서울시").build();
        Profile profile = Profile.builder().id("dummy ID").path("dummy path").name("dummy name").build();
        Member member = Member.builder().nickname("참여자").build();
        Member creator = Member.builder().nickname("생성자").profile(profile).build();

        em.persist(region);
        em.persist(member);
        em.persist(creator);

        /* ChatRoom & Chat create */
        List<ChatRoom> list = new ArrayList<>();
        for(int i=0; i<5; i++){
            ChatRoom room = ChatRoom.builder().name(i+"번째 방").member(creator).region(region).build();
            list.add(room);
            em.persist(room);

            for(int j=0; j<10; j++){
                Chat chat = Chat.builder().chatRoom(room).member(creator).content(i + "번 째 채팅이고, " + room.getId() + "번 방의 채팅").build();
                em.persist(chat);
            }
        }

        /* join ChatRoom */
        list.forEach(cr -> joinRepository.save(Join.builder().chatRoom(cr).member(member).build()));

        /* create Notifications */
        for(int i=1; i<=5; i++){
            long n = (long) i;

            for(int j=1; j<=i; j++) {
                Notification noti = Notification.builder().sender(creator).receiver(member).targetId(n).type(NotificationType.CHAT).confirm('0').build();
                em.persist(noti);
            }
        }

        //when
        em.flush();
        em.clear();
        System.out.println("--------------------------------------------------------");

        List resultList = joinRepository.getAsideChatRoomsWithJoinByMemberUsingNativeSQL(member);

        resultList.forEach(o -> {
            Object[] ob = (Object[]) o;

            System.out.println("방아이디 :" + ob[0] + " " + ob[0].getClass().getName());
            System.out.println("방이름 :" + ob[1] + " " + ob[1].getClass());
            System.out.println("방날짜 :" + ob[2] + " " + ob[2].getClass());
            System.out.println("방장아이디 :" + ob[3] + " " + ob[3].getClass().getName());
            System.out.println("방장닉네임 :" + ob[4] + " " + ob[4].getClass().getName());
            System.out.println("프로필아이디 :" + ob[5] + " " );
            System.out.println("프로필이름 :" + ob[6] + " ");
            System.out.println("프로필경로 :" + ob[7] + " " );
            System.out.println("지역아이디 :" + ob[8]);
            System.out.println("읽지않은알림개수 :" + ob[9] + " " + ob[9].getClass().getName());
            System.out.println("가장최근채팅내용 :" + ob[10]);
            System.out.println("가장최근채팅시간 :" + ob[11]);


            AsideChatRoomDTO dto = AsideChatRoomDTO.createDtoByObj(ob);

            System.out.println(dto);
            System.out.println("-----------------------");


        });

        //then
        assertThat(resultList.size()).isEqualTo(5);
    }
}