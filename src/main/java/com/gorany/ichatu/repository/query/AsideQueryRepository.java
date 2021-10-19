package com.gorany.ichatu.repository.query;

import com.gorany.ichatu.domain.Member;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Aside 화면에 fit하게 조회하기 위한 조회용 Repository
 * @author gorany
 * @version 1.0
 */
public interface AsideQueryRepository {

    /**
     * ASIDE의 채팅방 목록
     * 채팅방 [ID, 제목, 멤버, 멤버의 프로필, 지역, 채팅]
     * 멤버 [프로필]
     * 프로필[ID, 이름, 경로]
     * 채팅[내용, 날짜] (추후)
     * 지역[이름]
     * 조인[(내가 가입한 채팅방만), 날짜]
     * @param member 사용자
     * @return List 다양한 타입의 엔티티를 저장한 목록
     * */
    List<Object[]> getAsideChatRoomsWithJoinByMemberUsingNativeSQL(@Param("member") Member member);
}
