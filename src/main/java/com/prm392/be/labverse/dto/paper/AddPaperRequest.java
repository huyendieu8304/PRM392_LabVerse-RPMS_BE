package com.prm392.be.labverse.dto.paper;


public record AddPaperRequest(
        String s3Key,
        int totalPage,

        String authorName,
        String publicationYear,
        String title,
        String doi

        //todo them cac truong thong tin nua vao
) {
}
