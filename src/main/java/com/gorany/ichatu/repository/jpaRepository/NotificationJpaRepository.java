package com.gorany.ichatu.repository.jpaRepository;

import com.gorany.ichatu.domain.ChatRoom;
import com.gorany.ichatu.domain.Member;
import com.gorany.ichatu.domain.Notification;
import com.gorany.ichatu.exception.NoIdentityException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

/**
 * JPA -> Spring Data JPA로 넘어가며 Deprecated
 */
@Repository
@RequiredArgsConstructor
@Deprecated
public class NotificationJpaRepository {

    @PersistenceContext
    private final EntityManager em;

    public Optional<Notification> findById(Long id){
        return Optional.ofNullable(em.find(Notification.class, id));
    }

    public Long save(Notification notification){

        em.persist(notification);
        return notification.getId();
    }

    public void saveAll(List<Notification> notifications){

        notifications.forEach(em::persist);
    }

    //알림 목록 조회 (일부)
    public Optional<List<Notification>> findAllByMemberId(Member member, Integer page){
        int size = 10;
        int offset = (page - 1) * size;
        String query = "select nt from Notification nt " +
                "left join fetch nt.sender s " +
                "left join fetch s.profile " +
                "where nt.receiver = :receiver ";
                //"and nt.confirm = :value"; //미확인 알림만 조회하려고 했으나 변경

        return Optional.ofNullable(em.createQuery(query, Notification.class)
                .setParameter("receiver", member)
                //.setParameter("value", '0')
                .setMaxResults(size)
                .setFirstResult(offset)
                .getResultList());
    }

    //미확인 알림 몇 개인지 반환
    public Long getCount(Member receiver){
        return em.createQuery("select count(nt) from Notification nt where nt.receiver = :receiver and nt.confirm = '0'", Long.class)
                .setParameter("receiver", receiver)
                .getSingleResult();
    }

    //알림 확인 (단건)
    /**
     * 단건 알림 확인은 도메인에서 처리하기로 함.
     * @deprecated
     */
    @Deprecated
    public Integer update(Long notificationId){

        String query = "update Notification nt set nt.confirm = '1' where nt.id = :id";

        return em.createQuery(query).setParameter("id", notificationId).executeUpdate();
    }

    //알림 확인 (모두)
    public Integer updateAll(Member receiver){
        String query = "update Notification nt set nt.confirm = '1' where nt.receiver = :receiver and nt.confirm = :value";

        return em.createQuery(query)
                .setParameter("receiver", receiver)
                .setParameter("value", '0')
                .executeUpdate();
    }

    //알림 확인(채팅방)
    /**
     * @deprecated
     */
    @Deprecated
    public Integer updateByChatRoom(Member receiver, Long chatRoomId){
        String query = "update Notification nt set nt.confirm = '1' where nt.receiver = :receiver and nt.confirm = :value and nt.targetId = :targetId";

        return em.createQuery(query)
                .setParameter("targetId", chatRoomId)
                .setParameter("receiver", receiver)
                .setParameter("value", '0')
                .executeUpdate();
    }

    //확인한 알림 전부 제거
    public Integer deleteAll(Member receiver){
        String query = "delete from Notification nt where nt.receiver = :receiver and nt.confirm = '1'";

        return em.createQuery(query).setParameter("receiver", receiver).executeUpdate();
    }

    //특정 채팅방에 쌓인 알림 모두 확인 처리
    //사용자가 채팅방 입장 시 호출되는 메서드
    public Integer updateAllByChatRoomAndMember(Long chatRoomId, Member member){

        return em.createQuery("update Notification n set n.confirm = '1' where n.confirm = '0' and n.receiver = :member and n.targetId = :chatRoomId")
                .setParameter("member", member)
                .setParameter("chatRoomId", chatRoomId)
                .executeUpdate();
    }

    //채팅방에 의해 알림 모두 제거
    public Integer removeAllByChatRoom(ChatRoom chatRoom){
        return em.createQuery("delete from Notification n where n.targetId = :targetId")
                .setParameter("targetId", chatRoom.getId())
                .executeUpdate();
    }
}
