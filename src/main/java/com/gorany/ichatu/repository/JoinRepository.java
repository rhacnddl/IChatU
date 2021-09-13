package com.gorany.ichatu.repository;

import com.gorany.ichatu.domain.ChatRoom;
import com.gorany.ichatu.domain.Join;
import com.gorany.ichatu.domain.Member;
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
public class JoinRepository {

    @PersistenceContext
    private final EntityManager em;

    public Long save(Join join){

        em.persist(join);
        return join.getId();
    }

    /* 특정 채팅방에 가입한 사람의 ID 목록 반환 */
    public Optional<List<Long>> findMemberIdsByChatRoom(ChatRoom chatRoom){
        return Optional.ofNullable(em.createQuery("select j.member.id from Join j where j.chatRoom = :chatRoom", Long.class)
                .setParameter("chatRoom", chatRoom)
                .getResultList());
    }

    /* 특정 채팅방에 가입한 사람 Data 전체를 반환 */
    public Optional<List<Join>> findAllByChatRoom(ChatRoom chatRoom){

        return Optional.ofNullable( em.createQuery("select j from Join j " +
                "join fetch j.member " +
                "where j.chatRoom = :chatRoom", Join.class)
                .setParameter("chatRoom", chatRoom)
                .getResultList() );
    }

    public Optional<Join> findJoinByChatRoomAndMember(ChatRoom chatRoom, Member member){
        return Optional.ofNullable(em.createQuery("select j from Join j where j.member = :member and j.chatRoom = :chatRoom", Join.class)
                .setParameter("chatRoom", chatRoom)
                .setParameter("member", member)
                .getSingleResult());
    }

    public Optional<Join> findById(Long id){
        return Optional.ofNullable(em.find(Join.class, id));
    }

    /* A 채팅방에 유저 B가 가입했는지 여부 */
    public Optional<Boolean> findByChatRoomAndMember(ChatRoom chatRoom, Member member){
        return Optional.of(
                em.createQuery("select count(j) from Join j " +
                "where j.chatRoom = :chatRoom and j.member = :member", Long.class)
                .setParameter("chatRoom", chatRoom)
                .setParameter("member", member)
                .getSingleResult() == 1L);
    }

    public int remove(Join join){

        return em.createQuery("delete from Join j where j.member = :member and j.chatRoom = :chatRoom")
                .setParameter("member", join.getMember())
                .setParameter("chatRoom", join.getChatRoom())
                .executeUpdate();
    }

    /*
     * ASIDE의 채팅방 목록
     * 채팅방 [ID, 제목, 멤버, 멤버의 프로필, 지역, 채팅]
     * 멤버 [프로필]
     * 프로필[ID, 이름, 경로]
     * 채팅[내용, 날짜] (추후)
     * 지역[이름]
     * 조인[(내가 가입한 채팅방만), 날짜]
     */
    public Optional<List<Join>> getAsideChatRoomsWithJoinByMember(Member member){

        String query = "select j from Join j " +
                "join fetch j.chatRoom cr " +
                "join fetch cr.member m " +
                "left join fetch m.profile " +
                "join fetch cr.region " +
                "" +
                "where j.member = :member";

        return Optional.ofNullable(
                em.createQuery(query, Join.class)
                        .setParameter("member", member)
                        .getResultList()
        );
    }
    public Optional<List<Object>> getAsideChatRoomsWithJoinByMemberV2(Member member){

        String query = "select j, count(n) as count from Join j " +
                "join fetch j.chatRoom cr " +
                "join fetch cr.member m " +
                "left join fetch m.profile " +
                "join fetch cr.region " +
                "left join Notification n on n.targetId = cr.id " +
                "group by j, cr " +
                "having j.member = :member";

        return Optional.ofNullable(
                em.createQuery(query)
                        .setParameter("member", member)
                        .getResultList()
        );
    }
}
