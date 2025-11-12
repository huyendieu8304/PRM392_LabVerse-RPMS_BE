package com.prm392.be.labverse.dto.paper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor
public class PaperSummaryDTO {
    private String id;
    private String title;
    private String authorName;
    private String journalName;
    private int totalPage;
    private Integer currentPage;     // int hoặc Integer đều ok
    private String status;           // UNREAD / IN_PROGRESS / DONE
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
