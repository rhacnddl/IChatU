package com.gorany.ichatu.repository;

import com.gorany.ichatu.domain.ChatRoom;
import com.gorany.ichatu.domain.Region;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * ChatRoom 테이블을 이용하기 위한 Spring Data JPA Repository
 * @author gorany
 * @version 1.0
 */
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    /**
     * 각 지역의 채팅방 목록을 조회
     * Sort -> ChatRoomID 기준 DESC
     * @param pageable 페이징 및 정렬 정보
     * @param region 지역
     * @return Slice<ChatRoom> 채팅방 목록
     * */
    @Query("select cr from ChatRoom cr " +
            "left join fetch cr.region r " +
            "left join fetch cr.member m " +
            "left join fetch m.profile")
    Slice<ChatRoom> findAllBy(Pageable pageable, @Param("region") Region region);
    //Slice<ChatRoom> findAllByRegion(@Param("region") Region region, Pageable pageable);//추후 변경
}
