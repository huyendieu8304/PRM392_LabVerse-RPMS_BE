package com.prm392.be.labverse.dto.paperAnnotation;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class PaperAnnotationInfoResponse {
        private String id;
        private String annotationS3Key;
        private String updateAt;
}
