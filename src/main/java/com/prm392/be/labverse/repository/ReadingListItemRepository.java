package com.prm392.be.labverse.repository;

import com.prm392.be.labverse.entity.ReadingList;
import com.prm392.be.labverse.entity.ReadingListItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReadingListItemRepository extends JpaRepository<ReadingListItem, Long> {
    boolean existsByReadingListIdAndPaperId(String listId, String paperId);
    List<ReadingListItem> findByReadingListIdOrderByPositionAsc(String listId);
    int deleteByReadingListIdAndPaperId(String listId, String paperId);
    // dùng trong getPapers(String listId)

    // dùng trong getPapersAsSummaries(ReadingList rl)
    List<ReadingListItem> findAllByReadingListOrderByPositionAsc(ReadingList readingList);
    void deleteByReadingList_IdAndPaper_Id(String listId, String paperId);
    boolean existsByReadingList_IdAndPaper_Id(String listId, String paperId);
}