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
import javax.persistence.NoResultException;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceImplTest {

    @Autowired MemberService memberService;
    @Autowired EntityManager em;

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
    @DisplayName("회원가입 테스트")
    void signup() {

        //given
        MemberDTO origin = createMemberDTO("origin", "12345", "hello@world.com");
        MemberDTO sameNickname = createMemberDTO("origin", "12345", "another@world.com");

        //when
        Long id = memberService.signup(origin);

        //then
        assertThat(memberService.signup(sameNickname)).isEqualTo(-1L);
    }

    @Test
    @DisplayName("로그인 테스트")
    void login() {

        //given
        LocalDateTime now =  LocalDateTime.now();
        String nickname = "hello";
        String password = "12345";
        Member member = Member.builder().available(true).nickname(nickname).password(password).email("hello@email.com").build();
        em.persist(member);
        MemberDTO dto = createMemberDTO(nickname, password, "hello@email.com");

        em.flush();
        em.clear();
        //when
        MemberDTO match = memberService.login(dto);

        //then
        assertThat(match).isNotNull();
        assertThat(match.getLoginDate()).isAfter(now);
    }
    @Test
    @DisplayName("회원 수정 테스트")
    public void 회원수정테스트() throws Exception{

        //given
        MemberDTO dto = createMemberDTO("member", "12345", "hello-mail");
        Long memberId = memberService.signup(dto);
        String originProfileId = UUID.randomUUID().toString();
        Profile profile = Profile.builder().name("profile").path("random").id(originProfileId).build();
        Member member = em.find(Member.class, memberId);
        member.changeProfile(profile);

        em.flush();
        em.clear();

        ProfileDTO other = ProfileDTO.builder().name("other").path("random !!!").profileId(UUID.randomUUID().toString()).build();
        //when
        dto.setId(memberId);
        dto.setEmail("E-mail");
        dto.setProfileDTO(other);
        memberService.updateMember(dto);

        em.flush();
        em.clear();
        //then
        Member find = em.createQuery("select m from Member m join fetch m.profile where m = :member", Member.class)
                .setParameter("member", member)
                .getSingleResult();

        Profile p = em.find(Profile.class, originProfileId);
        assertThat(find.getEmail()).isEqualTo("E-mail");
        assertThat(find.getProfile().getName()).isEqualTo("other");
        assertThat(em.find(Profile.class, originProfileId)).isNull();
    }

    MemberDTO createMemberDTO(String nickname, String password, String email){
        return MemberDTO.builder()
                .nickname(nickname)
                .password(password)
                .email(email)
                .available(true)
                .role(Role.USER)
                .build();

    }
}