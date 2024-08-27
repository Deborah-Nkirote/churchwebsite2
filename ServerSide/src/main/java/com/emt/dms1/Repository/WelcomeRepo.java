package com.emt.dms1.Repository;

import com.emt.dms1.Models.WelcomeMessageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WelcomeRepo extends JpaRepository<WelcomeMessageRequest, Long> {
    Optional<WelcomeMessageRequest> findAllById(Long id);
}