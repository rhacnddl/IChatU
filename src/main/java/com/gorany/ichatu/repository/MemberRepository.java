package com.gorany.ichatu.repository;

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
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    @PersistenceContext
    private final EntityManager em;

    @Transactional
    public Long save(Member member) {

        em.persist(member);
        return member.getId();
    }

    public Optional<Member> findById(Long id){
        return Optional.ofNullable(em.find(Member.class, id));
    }

    @Transactional
    public Member match(Member member) {

        /* JPQL은 쓰기지연 저장소의 SQL을 flush 후 실행된다. */
        Member find = null;
        try {
            find = em.createQuery("select m from Member m where m.nickname = :nickname and m.password = :password", Member.class)
                    .setParameter("nickname", member.getNickname())
                    .setParameter("password", member.getPassword())
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        /* 로그인 성공이라 판단되면 최근 로그인 시간 갱신 */
        if(find.getId() != null) {
            find.updateLoginDate();
        }

        return find;
    }

    /* Member 정보 변경 Dirty Checking */
    @Transactional
    public void updateMember(Member member){
        Member origin = em.createQuery("select m from Member m left join fetch m.profile where m = :member", Member.class)
                .setParameter("member", member)
                .getSingleResult();

        Profile profile = member.getProfile();
        System.out.println("profile = " + profile);
        Profile originProfile = origin.getProfile();
        System.out.println("originProfile = " + originProfile);

        if(profile != null && originProfile != null)
            em.remove(origin.getProfile());

        origin.update(member);
        //em.persist(origin);
    }
}
