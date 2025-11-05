package com.prm392.be.labverse.repository;

import com.prm392.be.labverse.entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, Long> {
    Optional<InvalidatedToken> findByAccessToken(String accessToken);

    Integer deleteByExpiredAtBefore(LocalDateTime now);
}
