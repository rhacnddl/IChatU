package com.gorany.ichatu.repository;

import com.gorany.ichatu.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, String> {
}
