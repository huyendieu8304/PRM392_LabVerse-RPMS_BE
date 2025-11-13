package com.prm392.be.labverse.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames={"reading_list_id","paper_id"}))
public class ReadingListItem {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="reading_list_id", nullable=false)
    private ReadingList readingList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="paper_id", nullable=false)
    private Paper paper;

    @CreationTimestamp
    private LocalDateTime addedAt;

    @Column(nullable=false)
    private int position;
}
