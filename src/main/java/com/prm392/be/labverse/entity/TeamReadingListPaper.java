package com.prm392.be.labverse.entity;

import com.prm392.be.labverse.constant.EPriority;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "team_reading_list_paper")
public class TeamReadingListPaper {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "priority")
    EPriority priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paper_id", referencedColumnName = "id", nullable = false)
    Paper paper;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_reading_list_id", referencedColumnName = "id", nullable = false)
    TeamReadingList teamReadingList;
}
