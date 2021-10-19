package com.gorany.ichatu.repository;

import com.gorany.ichatu.domain.ChatRoom;
import com.gorany.ichatu.domain.Join;
import com.gorany.ichatu.domain.Member;
import com.gorany.ichatu.repository.query.AsideQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Joins 테이블을 이용하기 위한 Spring Data JPA Repository
 * @author gorany
 * @version 1.0
 */
public interface JoinRepository extends JpaRepository<Join, Long>, AsideQueryRepository {

    /**
     * 특정 채팅방에 가입한 사용자의 ID목록 반환 #페이징은 필요 없으므로 List반환
     * @param chatRoom 조회할 채팅방
     * @return List<Long> 멤버 ID목록
     * */
    @Query("select j.member.id from Join j where j.chatRoom = :chatRoom")
    List<Long> findMemberIdsByChatRoom(@Param("chatRoom") ChatRoom chatRoom);

    /**
     * 특정 채팅방에 가입한 데이터 전체를 반환
     * @param chatRoom 조회할 채팅방
     * @return List<Join> 채팅방에 참여한 데이터 목록
     * */
    @Query("select j from Join j join fetch j.member where j.chatRoom = :chatRoom")
    List<Join> findAllByChatRoom(@Param("chatRoom") ChatRoom chatRoom);

    /**
     * 멤버와 채팅방 정보로 Join 조회
     * @param chatRoom 채팅방
     * @param member 사용자
     * @return Optional<Join> Join 데이터
     * */
    Optional<Join> findByChatRoomAndMember(@Param("chatRoom") ChatRoom chatRoom, @Param("member") Member member);

    /**
     * A 채팅방에 사용자 B가 가입했는지 여부 조회
     * @param chatRoom 채팅방
     * @param member 사용자
     * @return Long 가입? 1 : 0
     * */
    @Query("select count(j) from Join j " +
            "where j.chatRoom = :chatRoom " +
            "and j.member = :member")
    Long getCountByChatRoomAndMember(@Param("chatRoom") ChatRoom chatRoom, @Param("member") Member member);

    /**
     * 채팅방이 제거될 때 Join 데이터 또한 모두 제거
     * @param chatRoom 채팅방
     * @return int Join이 제거된 개수
     * */
    @Modifying
    int removeAllByChatRoom(@Param("chatRoom") ChatRoom chatRoom);

    /**
     * 채팅방에 가입한 멤버와 멤버의 프로필을 조회
     * @param chatRoom 채팅방
     * @return List<Join> Join (+ Member (+ Profile)) 목록
     * */
    @Query("select j from Join j " +
            "join fetch j.member m " +
            "left join fetch m.profile " +
            "where j.chatRoom = :chatRoom")
    List<Join> getMembersAndProfile(@Param("chatRoom") ChatRoom chatRoom);

    /**
     * 채팅방 탈퇴 시, Join 제거
     * @param chatRoom 채팅방
     * @param member 멤버
     * */
    @Modifying
    void removeByChatRoomAndMember(@Param("chatRoom") ChatRoom chatRoom, @Param("member") Member member);
}
