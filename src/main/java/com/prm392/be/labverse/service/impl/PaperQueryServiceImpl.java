package com.prm392.be.labverse.service.impl;

import com.prm392.be.labverse.dto.dashboard.PaperCardDTO;
import com.prm392.be.labverse.entity.*;
import com.prm392.be.labverse.repository.*;
import com.prm392.be.labverse.service.PaperQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaperQueryServiceImpl implements PaperQueryService {

    private final PaperRepository paperRepo;
    private final ReadingStatusRepository rsRepo;
    private final FavoriteRepository favRepo;

    @Override
    public Page<PaperCardDTO> list(String userId, String filter, Pageable pageable) {
        return switch (filter) {
            case "recently_read" -> fromRecentlyRead(userId, pageable);
            case "favorites"     -> fromFavorites(userId, pageable);
            case "recently_added"-> fromRecentlyAdded(userId, pageable);
            default              -> fromRecentlyAdded(userId, pageable);
        };
    }

    private Page<PaperCardDTO> fromRecentlyAdded(String userId, Pageable pageable) {
        Page<Paper> page = paperRepo.findByUser_IdAndDeleteFlagFalseOrderByCreatedAtDesc(userId, pageable);
        Map<String, ReadingStatus> rsMap = preloadRs(userId, page.getContent());
        return page.map(p -> toDTO(userId, p, rsMap.get(p.getId())));
    }

    private Page<PaperCardDTO> fromRecentlyRead(String userId, Pageable pageable) {
        Page<ReadingStatus> page = rsRepo.pageByUserForRecentlyRead(userId, pageable);
        return page.map(rs -> toDTO(userId, rs.getPaper(), rs));
    }

    private Page<PaperCardDTO> fromFavorites(String userId, Pageable pageable) {
        Page<Favorite> page = favRepo.pageByUser(userId, pageable);
        List<Paper> papers = page.getContent().stream().map(Favorite::getPaper).toList();
        Map<String, ReadingStatus> rsMap = preloadRs(userId, papers);
        return page.map(f -> toDTO(userId, f.getPaper(), rsMap.get(f.getPaper().getId())));
    }

    private Map<String, ReadingStatus> preloadRs(String userId, List<Paper> papers) {
        if (papers.isEmpty()) return Map.of();
        List<String> ids = papers.stream().map(Paper::getId).toList();
        return rsRepo.findByUserAndPaperIds(userId, ids).stream()
                .collect(Collectors.toMap(rs -> rs.getPaper().getId(), rs -> rs, (a,b)->a));
    }

    private PaperCardDTO toDTO(String userId, Paper p, ReadingStatus rs) {
        int current = (rs == null) ? 0 : rs.getCurrentPage();
        int total = Math.max(p.getTotalPage(), 1);
        int progress = Math.min(100, (current * 100) / total);
        String status = (current <= 0) ? "UNREAD" : (current >= total) ? "FINISHED" : "READING";
        boolean favorite = favRepo.existsByUserIdAndPaperId(userId, p.getId());

        // Paper có thể CHƯA có field journalName → đổi lại cho khớp entity của bạn
        String journal = null;
        try {
            // nếu bạn thực sự có getJournalName():
            journal = p.getJournalName();
        } catch (NoSuchMethodError | Exception ignored) {
            // fallback nếu entity chỉ có doi/publicationYear
            journal = p.getDoi(); // hoặc để null
        }

        return PaperCardDTO.builder()
                .id(p.getId())
                .title(p.getTitle())
                .authors(p.getAuthorName())
                .journal(p.getJournalName())
                .status(status)
                .progress(progress)
                .favorite(favorite)
                .createdAt(p.getCreatedAt())
                .lastReadAt(rs == null ? null : rs.getLastReadAt())
                .build();
    }
}
