package com.prm392.be.labverse.controller;

import com.prm392.be.labverse.dto.S3SignedUrlResponse;
import com.prm392.be.labverse.dto.paper.AddPaperRequest;
import com.prm392.be.labverse.dto.paper.AddPaperResponse;
import com.prm392.be.labverse.dto.paper.PaperInfoResponse;
import com.prm392.be.labverse.dto.team.SetPaperPriorityRequest;
import com.prm392.be.labverse.dto.team.TeamReadingListPaperResponse;
import com.prm392.be.labverse.service.PaperService;
import com.prm392.be.labverse.service.TeamReadingListPaperService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/papers")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Validated
@Tag(name = "Paper", description = "API for paper-related operations")
public class PaperController {

    private final PaperService paperService;
    private final TeamReadingListPaperService teamReadingListPaperService;

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
    @GetMapping("/my-papers")
    public ResponseEntity<List<PaperInfoResponse>> getMyPapersOfCurrentUser() {
        return ResponseEntity.ok(paperService.getMyPapersOfCurrentUser());
    }

}
