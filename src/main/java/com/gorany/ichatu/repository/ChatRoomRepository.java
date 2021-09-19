package com.gorany.ichatu.repository;

import com.gorany.ichatu.domain.ChatRoom;
import com.gorany.ichatu.domain.Member;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class ChatRoomRepository {

    @PersistenceContext
    private EntityManager em;

    public Long save(ChatRoom chatRoom) {
        em.persist(chatRoom);

        return chatRoom.getId();
    }

    public Optional<ChatRoom> findById(Long id) {
        if(id == null) return Optional.empty();

        return Optional.ofNullable(em.find(ChatRoom.class, id));
    }

    public Long remove(ChatRoom chatRoom){
        em.remove(chatRoom);

        return chatRoom.getId();
    }

    public Optional<List<ChatRoom>> findAll(){

        String query = "select cr from ChatRoom cr " +
                "left join fetch cr.region r " +
                "left join fetch cr.member m " +
                "left join fetch m.profile";
        //where region = :region 추가 예정

        return Optional.ofNullable(em.createQuery(query, ChatRoom.class).getResultList());
    }
}
