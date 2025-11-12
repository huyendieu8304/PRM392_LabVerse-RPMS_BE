package com.prm392.be.labverse.dto.dashboard;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaperCardDTO {
    private String id;
    private String title;
    private String authors;
    private String journal;
    private int currentPage;
    private int totalPage;
    private boolean favorite;
    private int progress;         // derived
    private String status;        // derived
    private LocalDateTime createdAt;
    private LocalDateTime lastReadAt;

    public PaperCardDTO(
            String id,
            String title,
            String authors,
            String journal,
            int currentPage,
            int totalPage,
            boolean favorite,
            LocalDateTime createdAt,
            LocalDateTime lastReadAt
    ) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.journal = journal;
        this.currentPage = Math.max(0, currentPage);
        this.totalPage = Math.max(1, totalPage);
        this.favorite = favorite;
        this.createdAt = createdAt;
        this.lastReadAt = lastReadAt;

        // derived fields
        this.progress = Math.min(100, (this.currentPage * 100) / this.totalPage);
        if (this.currentPage <= 0) this.status = "UNREAD";
        else if (this.currentPage >= this.totalPage) this.status = "FINISHED";
        else this.status = "READING";
    }
}
