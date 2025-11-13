package com.prm392.be.labverse.service;

import com.prm392.be.labverse.dto.S3SignedUrlResponse;
import com.prm392.be.labverse.dto.dashboard.PaperCardDTO;
import com.prm392.be.labverse.dto.paper.AddPaperRequest;
import com.prm392.be.labverse.dto.paper.AddPaperResponse;
import com.prm392.be.labverse.dto.paper.PaperInfoResponse;
import com.prm392.be.labverse.dto.paper.PaperSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaperService {
    S3SignedUrlResponse getUploadUrl(String key);
    AddPaperResponse addPaper(AddPaperRequest request);
    S3SignedUrlResponse getDownloadUrl(String s3Key);
    PaperInfoResponse getPaperInfo(String id);

    Page<PaperCardDTO> listForDashboard(String userId, String filter, Pageable pageable);
    void updateReadingProgress(String userId, String paperId, int currentPage);
    void addFavorite(String userId, String paperId);
    void removeFavorite(String userId, String paperId);
    List<PaperSummaryDTO> listAllSummariesForUser(String userId);
    List<PaperInfoResponse> getMyPapersOfCurrentUser();
}
