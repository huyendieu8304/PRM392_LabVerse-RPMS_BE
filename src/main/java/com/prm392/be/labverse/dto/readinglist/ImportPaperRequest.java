package com.prm392.be.labverse.dto.readinglist;

public record ImportPaperRequest(String title, String authors, String abstractText, String s3Key, Integer position) {}