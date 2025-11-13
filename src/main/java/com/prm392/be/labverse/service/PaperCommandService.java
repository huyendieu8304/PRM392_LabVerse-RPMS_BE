package com.prm392.be.labverse.service;

import com.prm392.be.labverse.dto.paper.CreatePaperReq;
import com.prm392.be.labverse.entity.Paper;

public interface PaperCommandService {
    Paper create(CreatePaperReq req);
}
