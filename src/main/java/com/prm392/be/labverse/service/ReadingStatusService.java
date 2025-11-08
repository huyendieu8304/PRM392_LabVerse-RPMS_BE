package com.prm392.be.labverse.service;

import com.prm392.be.labverse.dto.readingStatus.ReadingStatusRequest;
import com.prm392.be.labverse.dto.readingStatus.ReadingStatusResponse;

public interface ReadingStatusService {

    ReadingStatusResponse getReadingStatusInfo(String userId, String paperId);
    ReadingStatusResponse createOrUpdate(ReadingStatusRequest request);

}
