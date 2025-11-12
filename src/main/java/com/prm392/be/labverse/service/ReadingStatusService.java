package com.prm392.be.labverse.service;

import com.prm392.be.labverse.dto.readingStatus.ReadingStatusRequest;
import com.prm392.be.labverse.dto.readingStatus.ReadingStatusResponse;
import com.prm392.be.labverse.dto.team.TeamReadingStatusResponse;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface ReadingStatusService {

    ReadingStatusResponse getReadingStatusInfo(String userId, String paperId);
    ReadingStatusResponse createOrUpdate(ReadingStatusRequest request);
    List<TeamReadingStatusResponse> getTeamReadingStatus(String teamId, String paperId);
}
