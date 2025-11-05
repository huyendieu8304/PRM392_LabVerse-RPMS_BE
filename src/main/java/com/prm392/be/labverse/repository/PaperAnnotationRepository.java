package com.prm392.be.labverse.repository;

import com.prm392.be.labverse.entity.PaperAnnotation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaperAnnotationRepository extends JpaRepository<PaperAnnotation, String> {
    Optional<PaperAnnotation> findByUserIdAndPaperId(String userId, String paperId);
}
