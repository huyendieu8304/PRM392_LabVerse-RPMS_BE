package com.prm392.be.labverse.service;

import com.prm392.be.labverse.dto.paper.PaperSummaryDTO;
import com.prm392.be.labverse.dto.readinglist.CreateReadingListRequest;
import com.prm392.be.labverse.dto.readinglist.ImportPaperRequest;
import com.prm392.be.labverse.dto.readinglist.ReadingListItemDTO;
import com.prm392.be.labverse.dto.readinglist.ReadingListResponse;
import com.prm392.be.labverse.entity.Paper;
import com.prm392.be.labverse.entity.ReadingList;
import com.prm392.be.labverse.entity.ReadingListItem;

import java.util.List;

public interface ReadingListService {
    ReadingListResponse create(CreateReadingListRequest req);
    ReadingListResponse rename(String id, String name, String description);
    void deleteList(String id);

    ReadingListItemDTO addExistingPaper(String listId, String paperId, Integer position);
//    ReadingListItem importPaperIntoList(String listId, ImportPaperRequest req);
    List<Paper> getPapers(String listId);
    void removePaper(String listId, String paperId);
    List<PaperSummaryDTO> getPapersAsSummaries(String userId, String listId);
    void removePaper(String userId, String listId, String paperId);
    List<ReadingListResponse> listMine(String userId);
}
