package com.prm392.be.labverse.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "paper_annotation",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "paper_id"})
        },
        indexes = {
                @Index(name = "idx_user_paper", columnList = "user_id, paper_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaperAnnotation {
    @Id
    @Column(nullable = false, updatable = false, unique = true)
    private String id;

    // Liên kết tới bảng User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    private User user;

    // Liên kết tới bảng Paper
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paper_id", updatable = false, nullable = false)
    private Paper paper;

    @Column(name = "annotation_s3_key", nullable = false)
    private String annotationS3Key;

    @Column(name = "updated_at", nullable = false)
    private String updatedAt; // UTC time

}
