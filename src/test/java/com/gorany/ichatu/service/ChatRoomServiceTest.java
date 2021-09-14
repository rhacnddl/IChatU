package com.gorany.ichatu.service;

import com.gorany.ichatu.domain.Member;
import com.gorany.ichatu.domain.Profile;
import com.gorany.ichatu.domain.Region;
import com.gorany.ichatu.dto.AsideChatRoomDTO;
import com.gorany.ichatu.dto.ChatRoomDTO;
import com.gorany.ichatu.dto.MemberDTO;
import com.gorany.ichatu.dto.ProfileDTO;
import com.gorany.ichatu.repository.RegionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ChatRoomServiceTest {

    @Autowired ChatRoomService chatRoomService;
    @Autowired MemberService memberService;
    @Autowired
    RegionRepository regionRepository;

    @Test
    @DisplayName("채팅방 조회 테스트")

    void getRooms() {

        //given
        Long regionId = regionRepository.save(Region.builder().name("도봉구").build());
        ProfileDTO profile = ProfileDTO.builder().profileId(UUID.randomUUID().toString()).name("test name").path("test path").build();
        MemberDTO member = MemberDTO.builder().
                nickname("test Nickname").password("12345").email("test email").build();

        //save Member
        Long memberId = memberService.signup(member);
        //find Member
        MemberDTO findMember = memberService.getOne(memberId);
        findMember.setProfileDTO(profile);

        //set Profile
        memberService.updateMember(findMember);

        MemberDTO findMember2 = memberService.getOne(memberId);

        IntStream.range(0, 50).forEach(i -> {
            ChatRoomDTO chatroom = ChatRoomDTO.builder().memberId(memberId).name("TEST CHATROOM " + i).regionId(regionId).build();
            chatRoomService.addRoom(chatroom);
        });
        System.out.println("-----given-----");
        //when
        List<ChatRoomDTO> rooms = chatRoomService.getRooms();

        System.out.println("-----when-----");
        //then
        rooms.forEach(System.out::println);
        assertThat(rooms.size()).isEqualTo(50);

    }

    @Test
    @DisplayName("어사이드에 들어갈 채팅방 목록 데이터 잘 가져오는지 테스트")
    void getRoomsAsideTest(){

        //given
        Long memberId = 1L;

        //when
        List<AsideChatRoomDTO> rooms = chatRoomService.getRoomsOnAside(memberId);

        //then
        rooms.forEach(System.out::println);
    }
}