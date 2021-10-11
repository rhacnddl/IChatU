package com.gorany.ichatu.repository;

import com.gorany.ichatu.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Profile 테이블을 이용하기 위한 Spring Data JPA Repository
 * @author gorany
 * @version 1.0
 */
public interface ProfileRepository extends JpaRepository<Profile, String> {
}
