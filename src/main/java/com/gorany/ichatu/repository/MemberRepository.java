package com.gorany.ichatu.repository;

import com.gorany.ichatu.domain.ChatRoom;
import com.gorany.ichatu.domain.Member;
import com.gorany.ichatu.domain.Profile;
import com.gorany.ichatu.exception.NoIdentityException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    @PersistenceContext
    private final EntityManager em;

    public Long save(Member member) {

        em.persist(member);

        return member.getId();
    }

    /* 회원가입 전, 닉네임이 중복되는지 Validation 용도 */
    public Long findByNickname(String nickname){

        return em.createQuery("select count(m) from Member m where m.nickname = :nickname", Long.class)
                .setParameter("nickname", nickname)
                .getSingleResult();
    }

    public Optional<Member> findById(Long id){
        return Optional.ofNullable(em.find(Member.class, id));
    }

    public Optional<Member> match(Member member) {

        /* JPQL은 쓰기지연 저장소의 SQL을 flush 후 실행된다. */
        List<Member> find = null;
        try {
            find = em.createQuery("select m from Member m left join fetch m.profile where m.nickname = :nickname and m.password = :password " +
                    "and m.available = true", Member.class)
                    .setParameter("nickname", member.getNickname())
                    .setParameter("password", member.getPassword())
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
        /* 로그인 성공이라 판단되면 최근 로그인 시간 갱신 */
        if(find.size() > 0) {
            Member result = find.get(0);
            result.updateLoginDate();
            return Optional.of(result);
        }

        return Optional.ofNullable(null);
    }

    /* Member 정보 변경 Dirty Checking
    * Select 1회(LEFT JOIN 1) - Member, Profile
    * Insert 1회 - Profile
    * Update 1회 - Member (dirty checking)
    * [Delete 1회] - Profile
    * */
    public void updateMember(Member member){
        Member origin = em.createQuery("select m from Member m left join fetch m.profile where m = :member", Member.class)
                .setParameter("member", member)
                .getSingleResult();

        Profile profile = member.getProfile();
        Profile originProfile = origin.getProfile();

        if(profile != null && originProfile != null)
            em.remove(origin.getProfile());

        origin.update(member);
//        em.persist(origin);
    }

    /*
    * Mypage에서 Member + Profile 정보 조회
    * */
    public Optional<Member> findMemberWithProfile(Member member){

        String query = "select m from Member m " +
                "left join fetch m.profile " +
                "where m = :member";

        return Optional.of(
                em.createQuery(query, Member.class)
                    .setParameter("member", member)
                    .getSingleResult()
        );
    }
}
