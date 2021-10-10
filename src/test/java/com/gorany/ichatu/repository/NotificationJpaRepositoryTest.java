package com.gorany.ichatu.repository;

import com.gorany.ichatu.domain.*;

import static org.assertj.core.api.Assertions.*;

import com.gorany.ichatu.repository.jpaRepository.NotificationJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@SpringBootTest
@Transactional
class NotificationJpaRepositoryTest {

    @Autowired
    NotificationJpaRepository repository;
    @Autowired EntityManager em;

    @Test
    @DisplayName("알림 INSERT 테스트")
    void saveAll() {
        //given
        List<Notification> list = new ArrayList<>();
        Member sender = Member.builder().role(Role.ADMIN).build();
        Member receiver = Member.builder().role(Role.USER).build();

        em.persist(sender);
        em.persist(receiver);

        IntStream.range(0, 100).forEach(i->{
            Notification noti = Notification.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .type(NotificationType.CHAT)
                    .targetId(1L)
                    .confirm('0')
                    .build();

            list.add(noti);
        });
        //when

        repository.saveAll(list);

        //then
        assertThat(list.get(0).getId()).isNotNull();
    }

    @Test
    @DisplayName("미확인 알림 몇 개인지 조회 테스트")
    void getCountTest(){

        //given
        List<Notification> list = new ArrayList<>();
        Member sender = Member.builder().role(Role.ADMIN).build();
        Member receiver = Member.builder().role(Role.USER).build();

        int wholeSize = 100;
        int confirmSize = 10;

        em.persist(sender);
        em.persist(receiver);

        IntStream.range(0, wholeSize).forEach(i->{
            Notification noti = Notification.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .type(NotificationType.CHAT)
                    .targetId(1L)
                    .confirm('0')
                    .build();

            list.add(noti);
        });

        repository.saveAll(list);

        for(int i=0; i<confirmSize; i++){
            repository.update(list.get(i).getId());
        }

        em.flush();
        em.clear();
        //when
        Long count = repository.getCount(receiver);

        //then
        assertThat(count).isEqualTo((long) (wholeSize - confirmSize));
    }

    @Test
    @DisplayName("특정 채팅방 입장 시 쌓인 알림 모두 확인 테스트")
    void 채팅방_입장_알림확인처리_테스트(){

        //given
        Member receiver = Member.builder().nickname("receiver").build();
        Member sender = Member.builder().nickname("sender").build();
        em.persist(receiver);
        em.persist(sender);

        Region region = Region.builder().name("서울시").build();
        em.persist(region);

        ChatRoom room = ChatRoom.builder().name("test Room").region(region).member(sender).build();
        em.persist(room);

        Join join1 = Join.builder().member(receiver).chatRoom(room).build();
        Join join2 = Join.builder().member(sender).chatRoom(room).build();
        em.persist(join1);
        em.persist(join2);

        List<Notification> list = new ArrayList<>();
        for(int i=0; i<50; i++){
            Notification nt = Notification.builder().type(NotificationType.CHAT).sender(sender).receiver(receiver).targetId(room.getId()).confirm('0').build();
            list.add(nt);
            em.persist(nt);
        }

        //when
        em.flush();
        em.clear();

        Integer count = repository.updateAllByChatRoomAndMember(room.getId(), receiver);
        //then
        List<Notification> ntList = repository.findAllByMemberId(receiver, 1).get();

        assertThat(ntList.get(0).getConfirm()).isEqualTo('1');
        assertThat(count).isEqualTo(50);
    }
}