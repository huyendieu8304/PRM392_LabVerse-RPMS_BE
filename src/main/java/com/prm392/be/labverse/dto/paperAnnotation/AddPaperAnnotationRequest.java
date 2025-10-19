package com.prm392.be.labverse.dto.paperAnnotation;

public record AddPaperAnnotationRequest(
        String id,
        String userId,
        String paperId,
        String annotationS3Key,
        String updateAt
) {
}
