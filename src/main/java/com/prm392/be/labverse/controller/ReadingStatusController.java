package com.prm392.be.labverse.controller;

import com.prm392.be.labverse.dto.readingStatus.ReadingStatusRequest;
import com.prm392.be.labverse.dto.readingStatus.ReadingStatusResponse;
import com.prm392.be.labverse.service.ReadingStatusService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reading-status")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Validated
@Tag(name = "Reading Status", description = "API for reading status operations")
public class ReadingStatusController {

    ReadingStatusService readingStatusService;

    @GetMapping("/")
    ResponseEntity<ReadingStatusResponse> getReadingStatus(
            @RequestParam String userId,
            @RequestParam String paperId
    ){
        return ResponseEntity.ok(readingStatusService.getReadingStatusInfo(userId, paperId));
    }

    @PostMapping("/create-or-update")
    ResponseEntity<ReadingStatusResponse> createOrUpdate(@RequestBody ReadingStatusRequest request){
        return ResponseEntity.ok(readingStatusService.createOrUpdate(request));
    }

}
