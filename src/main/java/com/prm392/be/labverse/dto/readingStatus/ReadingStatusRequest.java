package com.prm392.be.labverse.dto.readingStatus;

public record ReadingStatusRequest(
        String id,
        String userId,
        String paperId,
        int currentPage
) {
}
