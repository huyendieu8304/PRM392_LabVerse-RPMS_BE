package com.prm392.be.labverse.controller;

import com.prm392.be.labverse.dto.paper.PaperSummaryDTO;
import com.prm392.be.labverse.dto.readinglist.AddExistingPaperRequest;
import com.prm392.be.labverse.dto.readinglist.CreateReadingListRequest;
import com.prm392.be.labverse.dto.readinglist.ReadingListItemDTO;
import com.prm392.be.labverse.dto.readinglist.ReadingListResponse;
import com.prm392.be.labverse.dto.readinglist.UpdateReadingListRequest;
import com.prm392.be.labverse.security.CurrentUserInfo;
import com.prm392.be.labverse.service.ReadingListService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/reading-lists")
@RequiredArgsConstructor
@Validated
@Tag(name = "ReadingList", description = "API for personal reading lists")
public class ReadingListController {

    private final ReadingListService readingListService;

    // CREATE
    @PostMapping
    public ResponseEntity<ReadingListResponse> create(@Valid @RequestBody CreateReadingListRequest req) {
        ReadingListResponse resp = readingListService.create(req);
        return ResponseEntity.created(URI.create("/api/reading-lists/" + resp.id())).body(resp);
    }

    // RENAME/UPDATE
    // Controller
    @PutMapping("/{id}")
    public ResponseEntity<ReadingListResponse> rename(
            @PathVariable String id,
            @RequestBody UpdateReadingListRequest req
    ) {
        return ResponseEntity.ok(
                readingListService.rename(id, req.name(), req.description())
        );
    }


    // DELETE LIST
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        readingListService.deleteList(id);
        return ResponseEntity.noContent().build();
    }

    // LIST PAPERS IN LIST (as DTO)
    @GetMapping("/{id}/papers")
    public ResponseEntity<List<PaperSummaryDTO>> getPapers(
            @AuthenticationPrincipal CurrentUserInfo current,
            @PathVariable String id
    ) {
        if (current == null) return ResponseEntity.status(401).build();
        List<PaperSummaryDTO> dto = readingListService.getPapersAsSummaries(current.getUserId(), id);
        return ResponseEntity.ok(dto);
    }

    // ADD EXISTING PAPER INTO LIST (returns DTO)
    @PostMapping("/{id}/papers")
    public ResponseEntity<ReadingListItemDTO> addPaper(
            @AuthenticationPrincipal CurrentUserInfo current,
            @PathVariable String id,
            @RequestBody AddExistingPaperRequest req
    ) {
        if (current == null) return ResponseEntity.status(401).build();
        var dto = readingListService.addExistingPaper(id, req.paperId(), req.position());
        return ResponseEntity.created(URI.create("/api/reading-lists/" + id + "/papers/" + dto.itemId()))
                .body(dto);
    }


    // REMOVE PAPER FROM LIST (DUY NHẤT method này)
    @DeleteMapping("/{id}/papers/{paperId}")
    public ResponseEntity<Void> remove(
            @AuthenticationPrincipal CurrentUserInfo current,
            @PathVariable String id,
            @PathVariable String paperId
    ) {
        if (current == null) return ResponseEntity.status(401).build();
        readingListService.removePaper(current.getUserId(), id, paperId);
        return ResponseEntity.noContent().build(); // 204
    }

    // ReadingListController.java

    @GetMapping
    public ResponseEntity<List<ReadingListResponse>> listMine(
            @AuthenticationPrincipal CurrentUserInfo current
    ) {
        if (current == null) return ResponseEntity.status(401).build();
        List<ReadingListResponse> lists = readingListService.listMine(current.getUserId());
        return ResponseEntity.ok(lists);
    }

}
