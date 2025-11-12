package com.prm392.be.labverse.controller;

import com.prm392.be.labverse.dto.S3SignedUrlResponse;
import com.prm392.be.labverse.dto.dashboard.PaperCardDTO;
import com.prm392.be.labverse.dto.dashboard.UpdateProgressReq;
import com.prm392.be.labverse.dto.paper.AddPaperRequest;
import com.prm392.be.labverse.dto.paper.AddPaperResponse;
import com.prm392.be.labverse.dto.paper.PaperInfoResponse;
import com.prm392.be.labverse.dto.paper.PaperSummaryDTO;
import com.prm392.be.labverse.security.CurrentUserInfo;
import com.prm392.be.labverse.security.UserDetailsImpl;
import com.prm392.be.labverse.service.PaperService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/papers")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Validated
@Tag(name = "Paper", description = "API for paper-related operations")
public class PaperController {

    private final PaperService paperService;

    @GetMapping("/uploadUrl")
    public ResponseEntity<S3SignedUrlResponse> getUploadUrl(@RequestParam("key") String key){
        return ResponseEntity.ok(paperService.getUploadUrl(key));
    }

    @PostMapping
    public ResponseEntity<AddPaperResponse> addPaper(@RequestBody AddPaperRequest request){
        return ResponseEntity.ok(paperService.addPaper(request));
    }

    @GetMapping("/downloadUrl")
    public ResponseEntity<S3SignedUrlResponse> getDownloadUrl(@RequestParam("key") String s3key){
        return ResponseEntity.ok(paperService.getDownloadUrl(s3key));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaperInfoResponse> getPaperInfo(@PathVariable String id){
        return ResponseEntity.ok(paperService.getPaperInfo(id));
    }

    // --- API MỚI CHO DASHBOARD ---

    // GET /api/papers?userId=...&filter=recently_added|recently_read|favorites&page=0&size=20
    @GetMapping
    public ResponseEntity<Page<PaperCardDTO>> list(
            @AuthenticationPrincipal CurrentUserInfo current,
            @RequestParam(defaultValue = "recently_added") String filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        if (current == null) return ResponseEntity.status(401).build();
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(paperService.listForDashboard(current.getUserId(), filter, pageable));
    }

    // PATCH /api/papers/{paperId}/progress?userId=...
    @PatchMapping("/{paperId}/progress")
    public ResponseEntity<Void> updateProgress(
            @AuthenticationPrincipal CurrentUserInfo current,
            @PathVariable String paperId,
            @RequestBody UpdateProgressReq req
    ) {
        if (current == null) return ResponseEntity.status(401).build();
        paperService.updateReadingProgress(current.getUserId(), paperId, req.getCurrentPage());
        return ResponseEntity.noContent().build();
    }

    // POST /api/papers/{paperId}/favorite?userId=...
    @PostMapping("/{paperId}/favorite")
    public ResponseEntity<Void> addFavorite(
            @AuthenticationPrincipal CurrentUserInfo current,
            @PathVariable String paperId
    ) {
        if (current == null) return ResponseEntity.status(401).build();
        paperService.addFavorite(current.getUserId(), paperId);
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/papers/{paperId}/favorite?userId=...
    @DeleteMapping("/{paperId}/favorite")
    public ResponseEntity<Void> removeFavorite(
            @AuthenticationPrincipal CurrentUserInfo current,
            @PathVariable String paperId
    ) {
        if (current == null) return ResponseEntity.status(401).build();
        paperService.removeFavorite(current.getUserId(), paperId);
        return ResponseEntity.noContent().build();
    }

    // Lấy tất cả paper cho màn chọn (không cần userId, không phân trang)
    @GetMapping("/all")
    public ResponseEntity<List<PaperSummaryDTO>> listAllForCurrentUser(
            @AuthenticationPrincipal CurrentUserInfo current
    ) {
        if (current == null) return ResponseEntity.status(401).build();
        String userId = current.getUserId();
        return ResponseEntity.ok(paperService.listAllSummariesForUser(userId));
    }



}
