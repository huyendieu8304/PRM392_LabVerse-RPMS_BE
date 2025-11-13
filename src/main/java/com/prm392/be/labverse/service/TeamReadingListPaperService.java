package com.prm392.be.labverse.service;

import com.prm392.be.labverse.constant.EPriority;
import com.prm392.be.labverse.dto.team.TeamReadingListPaperResponse;

import java.util.List;

public interface TeamReadingListPaperService {
    TeamReadingListPaperResponse setPaperPriority(String teamId, String readingListId, String paperId, EPriority priority);
    List<TeamReadingListPaperResponse> listPapers(String teamId, String readingListId);
    TeamReadingListPaperResponse addPaper(String teamId, String readingListId, String paperId, EPriority priority);
    void removePaper(String teamId, String readingListId, String paperId);
}
