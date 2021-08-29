package com.gorany.ichatu.repository;

import com.gorany.ichatu.domain.Chat;
import com.gorany.ichatu.domain.ChatRoom;
import com.gorany.ichatu.domain.Join;
import com.gorany.ichatu.domain.Member;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class ChatRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void saveAll(List<Chat> chats){

        chats.forEach(em::persist);
    }

    @Transactional
    public Long save(Chat chat){
        em.persist(chat);

        return chat.getId();
    }

    public Chat findById(Long id){
        return em.find(Chat.class, id);
    }

    @Transactional
    public Long remove(Chat chat){
        em.remove(chat);

        return chat.getId();
    }


    /*
    * 채팅 내역을 가져올 때, 가져오는 사람의 Joins table -> regDate Column이 있어야함.
    * because -> regDate 이전 채팅 내역은 조회 X
    * 조회 시, Chat -> Member -> Profile 까지 땡겨온다. (fetch join)
    * */
    public List<Chat> findAllByChatRoomAndMember(ChatRoom chatRoom, Member member, Integer page){

        int limit = 20;
        int offset = (page - 1) * 20;

        String query = "select c " +
                "from Chat c " +
                "join fetch c.member m " +
                "left join fetch m.profile " +
                "where c.chatRoom = :chatRoom " +
                "and c.regDate > (" +
                "select j.regDate from Join j where j.member = :member and j.chatRoom = :chatRoom) ";

        return em.createQuery(query, Chat.class)
                .setParameter("chatRoom", chatRoom)
                .setParameter("member", member)
                .setMaxResults(limit)
                .setFirstResult(offset)
                .getResultList();
    }
}
