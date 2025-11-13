package com.prm392.be.labverse.service;

import com.prm392.be.labverse.dto.S3SignedUrlResponse;
import com.prm392.be.labverse.dto.paperAnnotation.AddPaperAnnotationRequest;
import com.prm392.be.labverse.dto.paperAnnotation.PaperAnnotationInfoResponse;
import com.prm392.be.labverse.entity.Paper;
import com.prm392.be.labverse.entity.PaperAnnotation;
import com.prm392.be.labverse.entity.User;
import com.prm392.be.labverse.exception.*;
import com.prm392.be.labverse.mapper.PaperAnnotationMapper;
import com.prm392.be.labverse.repository.PaperAnnotationRepository;
import com.prm392.be.labverse.repository.PaperRepository;
import com.prm392.be.labverse.repository.UserRepository;
import com.prm392.be.labverse.util.CurrentUserInfoUtil;
import com.prm392.be.labverse.util.FileStorageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaperAnnotationServiceImpl implements PaperAnnotationService {
    private final PaperAnnotationRepository annotationRepository;
    private final UserRepository userRepository;
    private final PaperRepository paperRepository;

    private final PaperAnnotationMapper mapper;
    private final FileStorageHelper fileStorageHelper;

    @Override
    public PaperAnnotationInfoResponse getPaperAnnotation(String userId, String paperId) {
        if (!CurrentUserInfoUtil.getCurrentUserId().equals(userId)){
            throw new AppException(AuthErrorCode.UNAUTHORIZED_REQUEST);
        }
        PaperAnnotation paperAnnotation = annotationRepository.findByUserIdAndPaperId(userId, paperId)
                .orElseThrow(() -> new AppException(PaperAnnotationErrorCode.ANNOTATION_NOT_FOUND));
        return mapper.toPaperAnnotationInfoResponse(paperAnnotation);
    }

    @Override
    public S3SignedUrlResponse getAnnotationDownloadUrl(String s3Key) {
        return new S3SignedUrlResponse(fileStorageHelper.generateDownloadFileUrl(s3Key));
    }

    @Override
    public PaperAnnotationInfoResponse addOrUpdatePaperAnnotation(AddPaperAnnotationRequest request) {

        String currentUserId = CurrentUserInfoUtil.getCurrentUserId();
        if (!Objects.equals(currentUserId, request.userId())){
            throw new AppException(AuthErrorCode.UNAUTHORIZED_REQUEST);
        }

//        Optional<PaperAnnotation> optionalPaperAnnotation = annotationRepository.findById(request.id());


        Optional<PaperAnnotation> optionalPaperAnnotation = annotationRepository.findByUserIdAndPaperId(currentUserId, request.paperId());

        PaperAnnotation paperAnnotation = new PaperAnnotation();

        if (optionalPaperAnnotation.isPresent()) {
            paperAnnotation = optionalPaperAnnotation.get();
            paperAnnotation.setUpdatedAt(request.updateAt());
        } else {
            //get user ra
            String userId = CurrentUserInfoUtil.getCurrentUserId();
            User user = userRepository.findByIdAndDeleteFlagFalse(userId)
                    .orElseThrow(() -> new AppException(UserErrorCode.ACCOUNT_NOT_FOUND));

            Paper paper = paperRepository.findByIdAndDeleteFlagFalse(request.paperId())
                    .orElseThrow(() -> new AppException(PaperErrorCode.PAPER_NOT_FOUND));
            paperAnnotation.setId(request.id());
            paperAnnotation.setUser(user);
            paperAnnotation.setPaper(paper);
            paperAnnotation.setAnnotationS3Key(request.annotationS3Key());
            paperAnnotation.setUpdatedAt(request.updateAt());
        }
        annotationRepository.save(paperAnnotation);
        return mapper.toPaperAnnotationInfoResponse(paperAnnotation);
    }

    @Override
    public S3SignedUrlResponse getAnnotationUploadUrl(String s3Key) {
        return new S3SignedUrlResponse(fileStorageHelper.generateUploadAnnotationUrl(s3Key));
    }


}
