package com.prm392.be.labverse.dto.paper;

import lombok.Data;

@Data
public class CreatePaperReq {
    private String userId;
    private String title;
    private String authorName;
    private String journalName;
    private String publicationYear;
    private String doi;
    private Integer totalPage; // optional, default 1
    private String s3Key;      // optional
}
