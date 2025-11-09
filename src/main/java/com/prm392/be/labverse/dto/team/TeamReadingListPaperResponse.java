package com.prm392.be.labverse.dto.team;

import com.prm392.be.labverse.constant.EPriority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamReadingListPaperResponse {
    private String id;
    private String readingListId;
    private String paperId;
    private EPriority priority;
    private String title;
    private String authorName;
}
