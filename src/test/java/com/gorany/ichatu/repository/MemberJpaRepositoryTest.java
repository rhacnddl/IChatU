package com.gorany.ichatu.repository;

import com.gorany.ichatu.domain.*;
import com.gorany.ichatu.repository.jpaRepository.MemberJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static com.gorany.ichatu.domain.Member.builder;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository repository;
    @Autowired EntityManager em;

    @Test
    @DisplayName("login 테스트")
    void match() {

        //given
        Member member = builder().nickname("gorany").password("12345").role(Role.ADMIN).available(Boolean.TRUE).build();
        repository.save(member);

        String nickname = "gorany";
        String password = "12345";
        Member source = builder().nickname(nickname).password(password).build();

        //when
        Member resultMember = repository.match(source).get();

        //then
        //같은 영속성 컨텍스트 내에서 동일한 객체를 가져온다
        em.flush();
        assertThat(member).isEqualTo(resultMember);

    }

    @Test
    @DisplayName("회원 정보 변경 테스트")
    void updateTest(){

        //given
        System.out.println("################# given");

        Member origin = builder()
                .nickname("TEST USER")
                .role(Role.ADMIN)
                .available(Boolean.TRUE)
                .password("TEST PASSWORD")
                .email("TEST EMAIL")
                .build();

        System.out.println("------ save ------");
        Long originId = repository.save(origin);

        em.flush();
        em.clear();

        Member update = builder()
                .id(originId)
                .nickname("UPDATE USER")
                .role(Role.USER)
                .available(Boolean.FALSE)
                .email("UPDATE EMAIL")
                .profile(Profile.builder().id("TEST").name("UPDATE PROFILE").path("UPDATE PATH").build())
                .password("UPDATE PASSWORD")
                .build();

        //when
        System.out.println("################# when");
        System.out.println("------ update ------");
        repository.updateMember(update);

        //then
        System.out.println("################# then");

        Member find = repository.findById(originId).get();
        System.out.println("find = " + find);
        String findProfileId = find.getProfile().getId();


        assertThat(find.getPassword()).isEqualTo("UPDATE PASSWORD");
        assertThat(find.getProfile().getName()).isEqualTo("UPDATE PROFILE");
        assertThat(find.getProfile().getId()).isNotNull();
    }

    @Test
    @DisplayName("멤버와 프로필 조회 테스트")
    public void 멤버_프로필_조회테스트() throws Exception{

        //given
        Member member = builder().nickname("test").email("test@naver.com").build();
        em.persist(member);
        Profile profile = Profile.builder().id("random").name("test Profile").path("test Path").build();
        member.changeProfile(profile);

        Long mid = member.getId();

        em.flush();
        em.clear();
        //when
        Member source = builder().id(mid).build();
        Member find = repository.findMemberWithProfile(source).get();

        //then
        assertThat(find.getId()).isEqualTo(member.getId());
        assertThat(find.getProfile().getId()).isEqualTo("random");

    }
}