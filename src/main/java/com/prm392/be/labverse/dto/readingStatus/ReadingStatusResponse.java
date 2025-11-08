package com.prm392.be.labverse.dto.readingStatus;

public record ReadingStatusResponse(
        String id,
        String userId,
        String paperId,
        int currentPage
) {
}
