package com.gorany.ichatu.repository;

import com.gorany.ichatu.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Region 테이블을 이용하기 위한 Spring Data JPA Repository
 * @author gorany
 * @version 1.0
 */
public interface RegionRepository extends JpaRepository<Region, Long> {
}
