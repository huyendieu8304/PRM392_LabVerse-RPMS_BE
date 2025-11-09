package com.prm392.be.labverse.repository;

import com.prm392.be.labverse.entity.TeamReadingListPaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamReadingListPaperRepository extends JpaRepository<TeamReadingListPaper, String> {
    Optional<TeamReadingListPaper> findByTeamReadingList_IdAndPaper_Id(String teamReadingListId, String paperId);
    boolean existsByTeamReadingList_IdAndPaper_Id(String teamReadingListId, String paperId);
    java.util.List<TeamReadingListPaper> findAllByTeamReadingList_Id(String teamReadingListId);
}