package com.prm392.be.labverse.dto.paper;

import lombok.*;

@NoArgsConstructor
@Setter
@Getter
public class PaperInfoResponse{

    private String id;
    private String s3Key;
    private int totalPage;
    private int currentPage;

    private String authorName;
    private String title;
    private String publicationYear;
    private String doi;
    //todo neu sau bo sung them truong nao thi them vao

}
