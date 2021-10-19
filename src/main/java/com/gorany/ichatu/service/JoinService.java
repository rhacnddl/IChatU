package com.gorany.ichatu.service;

import com.gorany.ichatu.dto.ChatRoomDTO;

import java.util.List;

public interface JoinService {

    /* A가 B 채팅방에 가입했는지 여부 조회 */
    Boolean checkJoinMember(Long chatRoomId, Long memberId);

    /* A가 B 채팅방에 가입 */
    Long joinChatRoom(Long chatRoomId, Long memberId);

    /* A가 B 채팅방 나가기 */
    void dropChatRoom(Long chatRoomId, Long memberId);

}
