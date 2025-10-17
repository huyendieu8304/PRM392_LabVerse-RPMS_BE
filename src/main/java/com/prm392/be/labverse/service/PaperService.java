package com.prm392.be.labverse.service;

import com.prm392.be.labverse.dto.S3SignedUrlResponse;
import com.prm392.be.labverse.dto.paper.AddPaperRequest;
import com.prm392.be.labverse.dto.paper.AddPaperResponse;

public interface PaperService {
    S3SignedUrlResponse getUploadUrl(String key);
    AddPaperResponse addPaper(AddPaperRequest request);
    S3SignedUrlResponse getDownloadUrl(String s3Key);
}
