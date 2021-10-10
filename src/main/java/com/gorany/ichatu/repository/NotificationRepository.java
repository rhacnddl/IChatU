package com.gorany.ichatu.repository;

import com.gorany.ichatu.domain.ChatRoom;
import com.gorany.ichatu.domain.Member;
import com.gorany.ichatu.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
* Notification 테이블을 이용하기 위한 Spring Data JPA Repository
* @author gorany
* @version 1.0
*/
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
    * 일부 알림 목록 조회
    * @param receiver 조회 요청자
    * @param pageable 페이징 및 정렬 정보
    * @return Slice<Notification> 알림 목록 ([더보기]로 페이징 할 것이므로 Page보다 Slice가 적합)
    */
    @Query(value = "select nt from Notification nt " +
            "left join fetch nt.sender s " +
            "left join fetch s.profile " +
            "where nt.receiver = :receiver ")
    Slice<Notification> findAllByMemberId(@Param("receiver") Member receiver, Pageable pageable);

    /**
     * 미확인 알림 개수 조회
     * @param receiver 조회 요청자
     * @return Long 미확인 알림 개수
     */
    @Query("select count(nt) from Notification nt where nt.receiver = :receiver and nt.confirm = '0'")
    Long getCount(@Param("receiver") Member receiver);

    /**
     * 모든 알림 확인
     * @param receiver 업데이트 요청자
     * @return int 업데이트된 개수
     */
    @Modifying
    @Query("update Notification nt set nt.confirm = '1' where nt.receiver = :receiver and nt.confirm = '0'")
    int updateAll(@Param("receiver") Member receiver);

    /**
     * 유저가 채팅방에 입장하면 해당 채팅방의 미확인 알림 모두 확인
     * @param receiver 업데이트 요청자
     * @param chatRoomId 채팅방 ID
     * @return int 업데이트된 개수
     */
    @Modifying
    @Query("update Notification n set n.confirm = '1' where n.confirm = '0' and n.receiver = :receiver and n.targetId = :chatRoomId")
    int updateAllByChatRoomAndMember(@Param("receiver") Member receiver, @Param("chatRoomId") Long chatRoomId);

    /**
     * 확인한 알림 모두 제거
     * @param receiver 제거 요청자
     */
    @Modifying
    @Query("delete from Notification nt where nt.receiver = :receiver and nt.confirm = '1'")
    void deleteAll(@Param("receiver") Member receiver);

    /**
     * 채팅방 제거될 때 해당 채팅방의 알림을 모두 제거
     * @param chatRoomId 제거되는 채팅방 ID
     */
    @Modifying
    @Query("delete from Notification n where n.targetId = :chatRoomId")
    void removeAllByChatRoom(@Param("chatRoomId") Long chatRoomId);
}
