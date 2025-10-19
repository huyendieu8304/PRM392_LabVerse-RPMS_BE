package com.prm392.be.labverse.mapper;

import com.prm392.be.labverse.dto.paperAnnotation.PaperAnnotationInfoResponse;
import com.prm392.be.labverse.entity.PaperAnnotation;
import org.springframework.stereotype.Component;

@Component
public class PaperAnnotationMapper {
    public PaperAnnotationInfoResponse toPaperAnnotationInfoResponse(PaperAnnotation annotation) {
        PaperAnnotationInfoResponse response = new PaperAnnotationInfoResponse();
        response.setId(annotation.getId());
        response.setAnnotationS3Key(annotation.getAnnotationS3Key());
        response.setUpdateAt(annotation.getUpdatedAt());
        return response;
    }
}
