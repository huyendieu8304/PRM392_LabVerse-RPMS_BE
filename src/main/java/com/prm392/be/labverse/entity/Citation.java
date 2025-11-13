package com.prm392.be.labverse.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "citation",
        indexes = @Index(name = "idx_citation_paper", columnList = "paper_id"))
public class Citation {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "paper_id", nullable = false)
    private Paper paper;

    @Column(nullable = false, length = 32)
    private String style;      // APA/MLA/Chicago…

    @Column(nullable = false, length = 4000)
    private String text;       // chuỗi citation đã render
}
