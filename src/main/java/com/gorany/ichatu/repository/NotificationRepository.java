package com.gorany.ichatu.repository;

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

@Repository
@RequiredArgsConstructor
public class NotificationRepository {

    @PersistenceContext
    private final EntityManager em;

    public Optional<Notification> findById(Long id){
        return Optional.ofNullable(em.find(Notification.class, id));
    }

    @Transactional
    public Long save(Notification notification){

        em.persist(notification);
        return notification.getId();
    }

    @Transactional
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
    @Transactional
    public Integer update(Long notificationId){

        String query = "update Notification nt set nt.confirm = '1' where nt.id = :id";

        return em.createQuery(query).setParameter("id", notificationId).executeUpdate();
    }

    //알림 확인 (모두)
    @Transactional
    public Integer updateAll(Member receiver){
        String query = "update Notification nt set nt.confirm = '1' where nt.receiver = :receiver and nt.confirm = :value";

        return em.createQuery(query)
                .setParameter("receiver", receiver)
                .setParameter("value", '0')
                .executeUpdate();
    }

    //확인한 알림 전부 제거
    @Transactional
    public Integer deleteAll(Member receiver){
        String query = "delete from Notification nt where nt.receiver = :receiver and nt.confirm = '1'";

        return em.createQuery(query).setParameter("receiver", receiver).executeUpdate();
    }
}