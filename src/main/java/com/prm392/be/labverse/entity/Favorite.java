package com.prm392.be.labverse.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "favorite",
        uniqueConstraints = @UniqueConstraint(name = "uk_favorite_user_paper",
                columnNames = {"user_id","paper_id"}),
        indexes = {
                @Index(name = "idx_fav_user", columnList = "user_id"),
                @Index(name = "idx_fav_paper", columnList = "paper_id"),
                @Index(name = "idx_fav_created", columnList = "createdAt")
        })
public class Favorite {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "paper_id", nullable = false)
    private Paper paper;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
