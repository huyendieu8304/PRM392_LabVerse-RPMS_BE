package com.prm392.be.labverse.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paper {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String s3Key;

    private boolean deleteFlag = false;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private int totalPage;
    @Column(nullable = false, columnDefinition = "int default 0")
    private int currentPage = 0;

    private String authorName;
    private String title;
    private String publicationYear;
    private String doi;
    //todo con thieu may cai lien quan toi trich dan
}
