package com.prm392.be.labverse.controller;


import com.prm392.be.labverse.dto.S3SignedUrlResponse;
import com.prm392.be.labverse.dto.paperAnnotation.AddPaperAnnotationRequest;
import com.prm392.be.labverse.dto.paperAnnotation.PaperAnnotationInfoResponse;
import com.prm392.be.labverse.service.PaperAnnotationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/paperAnnotations")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Validated
@Tag(name = "Annotation", description = "API for annotation-related operations")
public class PaperAnnotationController {
    private final PaperAnnotationService paperAnnotationService;

    @GetMapping
    public ResponseEntity<PaperAnnotationInfoResponse> getPaperAnnotation(
            @RequestParam("userId") String userId,
            @RequestParam("paperId") String paperId
    ){
        return ResponseEntity.ok(paperAnnotationService.getPaperAnnotation(userId, paperId));
    }

    @GetMapping("/downloadUrl")
    public ResponseEntity<S3SignedUrlResponse> getAnnotationDownloadUrl(@RequestParam(name = "s3Key") String s3Key){
        return ResponseEntity.ok(paperAnnotationService.getAnnotationDownloadUrl(s3Key));
    }

    @PostMapping
    public ResponseEntity<PaperAnnotationInfoResponse> addOrUpdatePaperAnnotation(@RequestBody AddPaperAnnotationRequest request){
        return ResponseEntity.ok(paperAnnotationService.addOrUpdatePaperAnnotation(request));
    }

    @GetMapping("/uploadUrl")
    public ResponseEntity<S3SignedUrlResponse> getAnnotationUploadUrl(@RequestParam(name = "s3Key") String s3Key){
        return ResponseEntity.ok(paperAnnotationService.getAnnotationUploadUrl(s3Key));
    }

}
