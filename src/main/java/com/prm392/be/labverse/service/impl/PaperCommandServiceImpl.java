package com.prm392.be.labverse.service.impl;

import com.prm392.be.labverse.dto.paper.CreatePaperReq;
import com.prm392.be.labverse.entity.Paper;
import com.prm392.be.labverse.repository.PaperRepository;
import com.prm392.be.labverse.repository.UserRepository;
import com.prm392.be.labverse.service.PaperCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaperCommandServiceImpl implements PaperCommandService {

    private final PaperRepository paperRepo;
    private final UserRepository userRepo;

    @Override
    @Transactional
    public Paper create(CreatePaperReq req) {
        var user = userRepo.findById(req.getUserId()).orElseThrow();
        var paper = Paper.builder()
                .user(user)
                .title(req.getTitle())
                .authorName(req.getAuthorName())
                .journalName(req.getJournalName())
                .publicationYear(req.getPublicationYear())
                .doi(req.getDoi())
                .s3Key(req.getS3Key())
                .totalPage(req.getTotalPage() == null ? 1 : Math.max(1, req.getTotalPage()))
                .currentPage(0)
                .build();
        return paperRepo.save(paper);
    }
}
