package com.prm392.be.labverse.dto.team;

public class AddPaperToReadingListRequest {
    private String paperId;

    public AddPaperToReadingListRequest() {}

    public AddPaperToReadingListRequest(String paperId) {
        this.paperId = paperId;
    }

    public String getPaperId() {
        return paperId;
    }

    public void setPaperId(String paperId) {
        this.paperId = paperId;
    }
}
