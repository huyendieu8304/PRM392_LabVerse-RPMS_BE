package com.prm392.be.labverse.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "reading_status",
        uniqueConstraints = @UniqueConstraint(name = "uk_reading_user_paper",
                columnNames = {"user_id","paper_id"}),
        indexes = {
                @Index(name = "idx_reading_user", columnList = "user_id"),
                @Index(name = "idx_reading_paper", columnList = "paper_id"),
                @Index(name = "idx_reading_last_read_at", columnList = "lastReadAt")
        })
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

    @Column(name="last_read_at")
    private LocalDateTime lastReadAt;
}
