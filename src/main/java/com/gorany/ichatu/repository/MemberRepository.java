package com.gorany.ichatu.repository;

import com.gorany.ichatu.domain.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Member 테이블을 이용하기 위한 Spring Data JPA Repository
 * @author gorany
 * @version 1.0
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * 회원가입 전, 닉네임이 중복되는지 Validation
     * @param nickname 요청 닉네임
     * @return Long 중복? 1 : 0
     * */
    @Query("select count(m) from Member m where m.nickname = :nickname")
    Long findCountByNickname(@Param("nickname") String nickname);

    /**
     * 로그인에 사용되는 메서드
     * @param nickname 요청 닉네임
     * @param password 요청 패스워드
     * @return Optional<Member> 매칭된 사용자 엔티티
     * */
    @Query("select m from Member m " +
            "left join fetch m.profile " +
            "where m.nickname = :nickname " +
            "and m.password = :password " +
            "and m.available = true")
    Optional<Member> match(@Param("nickname") String nickname, @Param("password") String password);

    /**
     * Mypage에서 Member + Profile 조회
     * @param member 요청자 엔티티
     * @return Optional<Member> Member + Profile
     * */
    @EntityGraph(attributePaths = {"profile"})
    @Query("select m from Member m where m = :member")
    Optional<Member> findMemberWithProfile(@Param("member") Member member);
}
