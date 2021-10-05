package com.gorany.ichatu.entity;

import com.gorany.ichatu.domain.Member;
import com.gorany.ichatu.domain.Profile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
public class MemberTest {

    @Test
    @DisplayName("회원 탈퇴 단일 테스트")
    public void 회원탈퇴_테스트() throws Exception{

        //given
        Profile profile = Profile.builder().id(UUID.randomUUID().toString()).name("파일").path("경로").build();
        Member member = Member.builder().id(1L).password("123456").nickname("test").email("test@naver.com").build();
        member.changeProfile(profile);

        //when
        member.deleteUser();

        //then
        assertThat(member.getNickname()).isEqualTo("탈퇴한 사용자");
        assertThat(member.getPassword()).isNull();
        assertThat(member.getProfile().getId()).isNull();
        assertThat(member.getProfile().getName()).isNull();
        assertThat(member.getProfile().getPath()).isNull();

        System.out.println("hello");
        System.out.println("hello");
        System.out.println("hello");
        System.out.println("hello");
        System.out.println("hello");
    }
}
