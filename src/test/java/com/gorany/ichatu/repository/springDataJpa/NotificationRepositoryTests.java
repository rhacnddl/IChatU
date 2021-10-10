package com.gorany.ichatu.repository.springDataJpa;

import com.gorany.ichatu.domain.*;
import com.gorany.ichatu.repository.NotificationRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Transactional
public class NotificationRepositoryTests {

    @Autowired NotificationRepository notificationRepository;
    @Autowired EntityManager em;

    @Test
    @DisplayName("채팅방 삭제시 해당 채팅방의 알림 모두 제거 테스트")
    public void 채팅방_삭제시_해당_채팅방_알림_제거() throws Exception{

        //given
        Region region = createRegion(); em.persist(region);
        Profile profile1 = createProfile("sender profile"); em.persist(profile1);
        Profile profile2 = createProfile("receiver profile"); em.persist(profile2);
        Member sender = createMember("sender", profile1); em.persist(sender);
        Member receiver = createMember("receiver", profile2); em.persist(receiver);
        ChatRoom room = createRoom(sender, "test room", region); em.persist(room);

        List<Notification> sources = new ArrayList<>();

        int total = 50;
        for(int i=0; i<total; i++){
            sources.add(createNotification(room.getId(), receiver, sender));
        }
        notificationRepository.saveAll(sources);

        em.flush();
        em.clear();
        //when
        notificationRepository.removeAllByChatRoom(room.getId());

        //then
        List<Notification> nts = notificationRepository.findAll();
        assertThat(nts.isEmpty()).isTrue();
    }
    @Test
    @DisplayName("확인된 알림 모두 제거 테스트")
    public void 확인한_알림_제거() throws Exception{

        //given
        Region region = createRegion(); em.persist(region);
        Profile profile1 = createProfile("sender profile"); em.persist(profile1);
        Profile profile2 = createProfile("receiver profile"); em.persist(profile2);
        Member sender = createMember("sender", profile1); em.persist(sender);
        Member receiver = createMember("receiver", profile2); em.persist(receiver);
        ChatRoom room = createRoom(sender, "test room", region); em.persist(room);

        List<Notification> sources = new ArrayList<>();

        int total = 50;
        for(int i=0; i<total; i++){
            sources.add(createNotification(room.getId(), receiver, sender));
        }
        notificationRepository.saveAll(sources);
        notificationRepository.updateAll(receiver);

        em.flush();
        em.clear();
        //when
        notificationRepository.deleteAll(receiver);

        //then
        List<Notification> nts = notificationRepository.findAll();
        assertThat(nts.isEmpty()).isTrue();
    }
    @Test
    @DisplayName("채팅방 입장시 미확인 알림 확인 처리 테스트")
    public void 채팅방_입장시_미확인_알림_확인() throws Exception{

        //given
        Region region = createRegion(); em.persist(region);
        Profile profile1 = createProfile("sender profile"); em.persist(profile1);
        Profile profile2 = createProfile("receiver profile"); em.persist(profile2);
        Member sender = createMember("sender", profile1); em.persist(sender);
        Member receiver = createMember("receiver", profile2); em.persist(receiver);
        ChatRoom room = createRoom(sender, "test room", region); em.persist(room);

        List<Notification> sources = new ArrayList<>();

        int total = 50;
        for(int i=0; i<total; i++){
            sources.add(createNotification(room.getId(), receiver, sender));
        }
        notificationRepository.saveAll(sources);

        em.flush();
        em.clear();
        //when
        int confirmCount = notificationRepository.updateAllByChatRoomAndMember(receiver, room.getId());

        //then
        assertThat(confirmCount).isEqualTo(total);
    }
    @Test
    @DisplayName("모든 미확인 알림 확인 테스트")
    public void 모든_미확인_알림_확인() throws Exception{

        //given
        Region region = createRegion(); em.persist(region);
        Profile profile1 = createProfile("sender profile"); em.persist(profile1);
        Profile profile2 = createProfile("receiver profile"); em.persist(profile2);
        Member sender = createMember("sender", profile1); em.persist(sender);
        Member receiver = createMember("receiver", profile2); em.persist(receiver);
        ChatRoom room = createRoom(sender, "test room", region); em.persist(room);

        List<Notification> sources = new ArrayList<>();

        int total = 50;
        for(int i=0; i<total; i++){
            sources.add(createNotification(room.getId(), receiver, sender));
        }
        notificationRepository.saveAll(sources);

        em.flush();
        em.clear();
        //when
        int confirmCount = notificationRepository.updateAll(receiver);

        //then
        Notification nt = notificationRepository.findAll().get(0);

        assertThat(confirmCount).isEqualTo(total);
        assertThat(nt.getConfirm()).isEqualTo('1');
    }
    @Test
    @DisplayName("미확인 알림 개수 조회 + Notification 도메인 비즈니스 메서드 confirm() 테스트")
    public void 미확인_알림_개수_조회() throws Exception{

        //given
        Region region = createRegion(); em.persist(region);
        Profile profile1 = createProfile("sender profile"); em.persist(profile1);
        Profile profile2 = createProfile("receiver profile"); em.persist(profile2);
        Member sender = createMember("sender", profile1); em.persist(sender);
        Member receiver = createMember("receiver", profile2); em.persist(receiver);
        ChatRoom room = createRoom(sender, "test room", region); em.persist(room);

        List<Notification> sources = new ArrayList<>();

        int total = 50;
        for(int i=0; i<total; i++){
            sources.add(createNotification(room.getId(), receiver, sender));
        }
        notificationRepository.saveAll(sources);

        int start = 20;
        int end = 30;
        for(int i=start; i<end; i++)
            sources.get(i).confirm();

        em.flush();
        em.clear();
        //when
        Long count = notificationRepository.getCount(receiver);

        //then
        assertThat(count).isEqualTo(total - (end - start));

    }
    @Test
    @DisplayName("알림목록 조회 테스트 + Paging + Sort")
    public void 알림_목록_조회() throws Exception{

        //given
        Region region = createRegion(); em.persist(region);
        Profile profile1 = createProfile("sender profile"); em.persist(profile1);
        Profile profile2 = createProfile("receiver profile"); em.persist(profile2);
        Member sender = createMember("sender", profile1); em.persist(sender);
        Member receiver = createMember("receiver", profile2); em.persist(receiver);
        ChatRoom room = createRoom(sender, "test room", region); em.persist(room);

        List<Notification> sources = new ArrayList<>();

        for(int i=0; i<50; i++){
            sources.add(createNotification(room.getId(), receiver, sender));
        }
        notificationRepository.saveAll(sources);

        em.flush();
        em.clear();

        int page = 0; //Pageable start from zero
        int size = 10;
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        //when
        Slice<Notification> find = notificationRepository.findAllByMemberId(receiver, pageable);
        List<Notification> result = find.getContent();

        //then
        assertThat(result.size()).isEqualTo(size); //current 개수
        assertThat(find.getNumber()).isEqualTo(page); //current page
        assertThat(find.getNumberOfElements()).isEqualTo(size); //현재 content의 개수
        assertThat(result.get(0).getId()).isGreaterThan(result.get(1).getId());
    }
    private Region createRegion(){
        return Region.builder()
                .name("Seoul")
                .build();
    }
    private Profile createProfile(String name){
        return Profile.builder()
                .name(name)
                .path("random path")
                .id(UUID.randomUUID().toString())
                .build();
    }
    private ChatRoom createRoom(Member member, String name, Region region){
        return ChatRoom.builder()
                .member(member)
                .name(name)
                .region(region)
                .build();
    }
    private Member createMember(String nickname, Profile profile){
        return Member.builder()
                .nickname(nickname)
                .profile(profile)
                .build();
    }
    private Notification createNotification(Long targetId, Member receiver, Member sender){
        return Notification.builder()
                .receiver(receiver)
                .sender(sender)
                .targetId(targetId)
                .confirm('0')
                .type(NotificationType.CHAT)
                .build();
    }
}
