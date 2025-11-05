package com.prm392.be.labverse.service;

import com.prm392.be.labverse.dto.S3SignedUrlResponse;
import com.prm392.be.labverse.dto.paperAnnotation.AddPaperAnnotationRequest;
import com.prm392.be.labverse.dto.paperAnnotation.PaperAnnotationInfoResponse;

public interface PaperAnnotationService {

    PaperAnnotationInfoResponse getPaperAnnotation(String userId, String paperId);
    S3SignedUrlResponse getAnnotationDownloadUrl(String s3Key);
    PaperAnnotationInfoResponse addOrUpdatePaperAnnotation(AddPaperAnnotationRequest request);
    S3SignedUrlResponse getAnnotationUploadUrl(String s3Key);
}
