package com.gorany.ichatu.repository.jpaRepository;

import com.gorany.ichatu.domain.Region;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * JPA -> Spring Data JPA로 인한 Deprecated
 * @deprecated
 * @author gorany
 * @version 1.0
 * */
@Deprecated
@Repository
public class RegionJpaRepository {

    @PersistenceContext
    private EntityManager em;

    public Long save(Region region){
        em.persist(region);

        return region.getId();
    }
}
