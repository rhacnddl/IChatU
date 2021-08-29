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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class NotificationRepositoryTest {

    @Autowired NotificationRepository repository;
    @Autowired MemberRepository memberRepository;

    @Test
    @DisplayName("알림 Bulk Insert Test")
    @Rollback(false)
    void saveAll() {
        //given
        List<Notification> list = new ArrayList<>();
        Member sender = Member.builder().role(Role.ADMIN).build();
        Member receiver = Member.builder().role(Role.USER).build();

        memberRepository.save(sender);
        memberRepository.save(receiver);

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
        assertThat(list.get(0).getId()).isNotNull().isEqualTo(1L);
    }

    @Test
    @DisplayName("미확인 알림 몇 개인지 테스트")
    void getCountTest(){

        //given
        Member receiver = Member.builder().id(1L).build();

        //when
        Long count = repository.getCount(receiver);

        //then
        assertThat(count).isGreaterThan(0L);
    }
}