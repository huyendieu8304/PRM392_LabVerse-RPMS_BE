package com.prm392.be.labverse.repository;

import com.prm392.be.labverse.entity.ReadingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReadingStatusRepository extends JpaRepository<ReadingStatus, String> {
    Optional<ReadingStatus> findByUserIdAndPaperId(String userId, String paperId);
}
