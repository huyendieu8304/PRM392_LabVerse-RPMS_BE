package com.prm392.be.labverse.mapper;

import com.prm392.be.labverse.dto.paper.PaperInfoResponse;
import com.prm392.be.labverse.entity.Paper;
import org.springframework.stereotype.Component;

@Component
public class PaperMapper {
    public PaperInfoResponse toPaperInfoResponse(Paper paper) {
        PaperInfoResponse response = new PaperInfoResponse();
        response.setId(paper.getId());
        response.setS3Key(paper.getS3Key());
        response.setTotalPage(paper.getTotalPage());
        response.setCurrentPage(paper.getCurrentPage());

        response.setAuthorName(paper.getAuthorName());
        response.setTitle(paper.getTitle());
        response.setPublicationYear(paper.getPublicationYear());
        response.setDoi(paper.getDoi());
        //todo set tiep neu them truong
        return response;
    }
}
