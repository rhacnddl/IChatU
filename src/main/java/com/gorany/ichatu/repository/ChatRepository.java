package com.gorany.ichatu.repository;

import com.gorany.ichatu.domain.Chat;
import com.gorany.ichatu.domain.ChatRoom;
import com.gorany.ichatu.domain.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Chat 테이블을 이용하기 위한 Spring Data JPA Repository
 * @author gorany
 * @version 1.0
 */
public interface ChatRepository extends JpaRepository<Chat, Long> {

    /**
     * 채팅방에 저장된 채팅 내역을 조회
     * - 채팅방에 가입한 날짜 이후의 채팅만 조회할 수 있어야한다.
     * - Chat + Chat 작성자 + 작성자의 Profile join
     * @param chatRoom 채팅방
     * @param member 조회 요청자
     * @param pageable 페이징 및 정렬(limit 20, chat_id DESC)
     * @return Slice<Chat> 채팅 내역
     */
    @Query("select c from Chat c " +
            "join fetch c.member m " +
            "left join fetch m.profile " +
            "where c.chatRoom = :chatRoom " +
            "and c.regDate > (" +
                "select j.regDate from Join j where j.member = :member and j.chatRoom = :chatRoom" +
            ")")
    Slice<Chat> findAllByChatRoomAndMember(@Param("chatRoom") ChatRoom chatRoom,
                                           @Param("member") Member member,
                                           @Param("pageable") Pageable pageable);
}