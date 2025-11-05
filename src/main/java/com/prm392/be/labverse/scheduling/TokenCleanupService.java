package com.prm392.be.labverse.scheduling;

import com.prm392.be.labverse.repository.InvalidatedTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TokenCleanupService {
    private final Logger log = LoggerFactory.getLogger(TokenCleanupService.class);
    private final InvalidatedTokenRepository invalidatedTokenRepository;

    public TokenCleanupService(InvalidatedTokenRepository invalidatedTokenRepository) {
        this.invalidatedTokenRepository = invalidatedTokenRepository;
    }

    /**
     * Job chạy vào phút 0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55 của mỗi giờ
     * Cron format: second minute hour day month weekday
     */
    @Scheduled(cron = "0 0/5 * * * *")    @Transactional
    public void cleanupExpiredTokens() {
        int deleted = invalidatedTokenRepository.deleteByExpiredAtBefore(LocalDateTime.now());
        log.info("Cleanup job executed, deleted " + deleted + " expired/revoked tokens");
    }
}
