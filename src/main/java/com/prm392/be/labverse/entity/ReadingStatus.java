package com.prm392.be.labverse.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "paper_id"})
        },
        indexes = {
                @Index(name = "idx_user_paper", columnList = "user_id, paper_id")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadingStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paper_id", nullable = false)
    private Paper paper;

    @Column(name = "current_page")
    private int currentPage;
}
