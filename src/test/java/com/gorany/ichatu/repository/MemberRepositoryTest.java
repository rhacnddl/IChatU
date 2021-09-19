package com.gorany.ichatu.repository;

import com.gorany.ichatu.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MemberRepositoryTest {

    @Autowired MemberRepository repository;
    @Autowired EntityManager em;

    @Test
    @DisplayName("login 테스트")
    void match() {

        //given
        Member member = Member.builder().nickname("gorany").password("12345").role(Role.ADMIN).available(Boolean.TRUE).build();
        repository.save(member);

        String nickname = "gorany";
        String password = "12345";
        Member source = Member.builder().nickname(nickname).password(password).build();

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

        Member origin = Member.builder()
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

        Member update = Member.builder()
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

}