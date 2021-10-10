package com.gorany.ichatu.repository.springDataJpa;

import com.gorany.ichatu.domain.ChatRoom;
import com.gorany.ichatu.domain.Member;
import com.gorany.ichatu.domain.Profile;
import com.gorany.ichatu.domain.Region;
import com.gorany.ichatu.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Transactional
public class MemberRepositoryTests {

    @Autowired MemberRepository repository;
    @Autowired EntityManager em;

    @Test
    @DisplayName("닉네임 중복 여부 테스트")
    public void 닉네임_중복_테스트() throws Exception{

        //given
        String nickname = "origin";
        Member origin = createMember(nickname, "12345", null); em.persist(origin);

        em.flush();
        em.clear();
        //when
        Long count = repository.findCountByNickname(nickname);

        //then
        assertThat(count).isEqualTo(1);
    }
    @Test
    @DisplayName("로그인 테스트")
    public void 로그인_테스트() throws Exception{

        //given
        String nickname = "A";
        String password = "12345";
        Member member = createMember(nickname, password,null);
        repository.save(member);
        Long id = member.getId();

        em.flush();
        em.clear();
        //when
        Optional<Member> result = repository.match(nickname, password);
        //then
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.orElse(null)).isNotNull();

        result.ifPresent(Member::updateLoginDate);

        em.flush();
    }
    @Test
    @DisplayName("Mypage에서 Member + Profile조회 테스트")
    public void 마이페이지_조회_테스트() throws Exception{

        //given
        String nickname = "origin";
        Member origin = createMember(nickname, "12345", null); em.persist(origin);
        Profile profile = createProfile("profile", origin); em.persist(profile);
        origin.changeProfile(profile);

        em.flush();
        em.clear();
        //when
        Member result = repository.findMemberWithProfile(origin).get();
        //then
        assertThat(result.getProfile().getClass().getName()).isEqualTo(Profile.class.getName());
        assertThat(result.getNickname()).isEqualTo(nickname);

    }
    private Profile createProfile(String name, Member member){
        return Profile.builder()
                .name(name)
                .path("random path")
                .member(member)
                .id(UUID.randomUUID().toString())
                .build();
    }
    private Member createMember(String nickname, String password, Profile profile){
        return Member.builder()
                .nickname(nickname)
                .profile(profile)
                .available(true)
                .password(password)
                .build();
    }
}
