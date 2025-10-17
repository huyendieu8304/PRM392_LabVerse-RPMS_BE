package com.prm392.be.labverse.controller;

import com.prm392.be.labverse.dto.S3SignedUrlResponse;
import com.prm392.be.labverse.dto.paper.AddPaperRequest;
import com.prm392.be.labverse.dto.paper.AddPaperResponse;
import com.prm392.be.labverse.service.PaperService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
}
