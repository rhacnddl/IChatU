package com.gorany.ichatu.repository;

import com.gorany.ichatu.domain.Region;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class RegionRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Long save(Region region){
        em.persist(region);

        return region.getId();
    }
}
