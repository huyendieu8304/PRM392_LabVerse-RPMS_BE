package com.prm392.be.labverse.dto.readinglist;

import java.time.LocalDateTime;

public record ReadingListResponse(
        String id,
        String name,
        String description,
        int paperCount,
        LocalDateTime createdAt
) {}
