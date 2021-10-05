package com.gorany.ichatu.service;

import com.gorany.ichatu.domain.Member;
import com.gorany.ichatu.domain.Profile;
import com.gorany.ichatu.domain.Role;
import com.gorany.ichatu.dto.MemberDTO;
import com.gorany.ichatu.dto.MemberDropRequest;
import com.gorany.ichatu.dto.ProfileDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MemberServiceImplTest {

    @Autowired MemberService memberService;
    @Autowired EntityManager em;

//    @Test
//    @DisplayName("Member만 조회 후 entityToDTO() 했을 때 Profile을 따로 조회하는 지 안하는지 테스트")
//    @Transactional
//    void getOneTest(){
//
//        //given
//        ProfileDTO profileDTO = ProfileDTO.builder().name("profile").path("profile").build();
//        MemberDTO member = MemberDTO.builder().nickname("TEST A").email("TEST EM").profileDTO(profileDTO).build();
//        Long memberId = memberService.signup(member);
//
//        //when
//        MemberDTO find = memberService.getOne(memberId);
//
//        //then
//        System.out.println("Analyze the query...");
//        assertThat(find.getId()).isEqualTo(memberId);
//    }

    @Test
    @DisplayName("회원탈퇴 테스트")
    public void 회원탈퇴_테스트() {

        //given
        Profile profile = Profile.builder().id(UUID.randomUUID().toString()).name("파일").path("경로").build();
        Member member = Member.builder().nickname("test").password("12345").email("test@naver.com").build();
        em.persist(member);

        Long id = member.getId();
        member.changeProfile(profile);

        em.flush();
        em.clear();

        MemberDropRequest request = new MemberDropRequest();
        request.setId(id);
        request.setPassword("12345");

        em.flush();
        em.clear();
        //when
        Boolean result = memberService.dropMember(request);

        //then
        Member find = em.find(Member.class, member.getId());
        assertThat(result).isTrue();
        assertThat(find.getNickname()).isEqualTo("탈퇴한 사용자");
        assertThat(find.getEmail()).isNull();
    }

    @Test
    void signup() {
    }

    @Test
    void login() {
    }

//    @Test
//    void updateMember() {
//
//        //given
//        MemberDTO sample = MemberDTO.builder().nickname("TEST").email("TESTEMAIL").role(Role.USER).password("123").build();
//        Long sampleId = memberService.signup(sample);
//
//        ProfileDTO profileDTO = ProfileDTO.builder().name("TEST NAME").path("TEST PATH").build();
//
//        MemberDTO sample2 = MemberDTO.builder().profileDTO(profileDTO).id(sampleId).nickname("UPDATE").email("UPDATEEMAIL").role(Role.ADMIN).password("321").build();
//        //when
//        memberService.updateMember(sample2);
//
//        //then
//        MemberDTO one = memberService.getOne(sampleId);
//        assertThat(one.getProfileDTO().getProfileId()).isNotNull();
//    }
}