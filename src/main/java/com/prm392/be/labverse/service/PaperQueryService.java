package com.prm392.be.labverse.service;

import com.prm392.be.labverse.dto.dashboard.PaperCardDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaperQueryService {
    Page<PaperCardDTO> list(String userId, String filter, Pageable pageable);
}
