package com.gorany.ichatu.repository;

import com.gorany.ichatu.domain.Member;
import com.gorany.ichatu.domain.Notification;
import com.gorany.ichatu.domain.NotificationType;
import com.gorany.ichatu.domain.Role;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class NotificationRepositoryTest {

    @Autowired NotificationRepository repository;
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
}