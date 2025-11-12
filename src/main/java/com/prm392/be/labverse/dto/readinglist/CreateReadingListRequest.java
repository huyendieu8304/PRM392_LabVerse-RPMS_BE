package com.prm392.be.labverse.dto.readinglist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateReadingListRequest(
        @NotBlank @Size(max = 120)
        String name,
        @Size(max = 2000)
        String description
) {}
