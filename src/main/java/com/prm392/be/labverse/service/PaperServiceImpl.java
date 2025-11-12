package com.prm392.be.labverse.service;

import com.prm392.be.labverse.constant.ERole;
import com.prm392.be.labverse.dto.S3SignedUrlResponse;
import com.prm392.be.labverse.dto.paper.AddPaperRequest;
import com.prm392.be.labverse.dto.paper.AddPaperResponse;
import com.prm392.be.labverse.dto.paper.PaperInfoResponse;
import com.prm392.be.labverse.entity.Paper;
import com.prm392.be.labverse.entity.User;
import com.prm392.be.labverse.exception.AppException;
import com.prm392.be.labverse.exception.AuthErrorCode;
import com.prm392.be.labverse.exception.PaperErrorCode;
import com.prm392.be.labverse.exception.UserErrorCode;
import com.prm392.be.labverse.mapper.PaperMapper;
import com.prm392.be.labverse.repository.PaperRepository;
import com.prm392.be.labverse.repository.UserRepository;
import com.prm392.be.labverse.util.CurrentUserInfoUtil;
import com.prm392.be.labverse.util.FileStorageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaperServiceImpl implements PaperService {

    private final PaperRepository paperRepository;
    private final UserRepository userRepository;
    private final FileStorageHelper fileStorageHelper;
    private final PaperMapper paperMapper;

    @Override
    public S3SignedUrlResponse getUploadUrl(String key) {
        return new S3SignedUrlResponse(fileStorageHelper.generateUploadPaperUrl(key));
    }

    @Override
    public AddPaperResponse addPaper(AddPaperRequest request) {
        //get user thực hiện request hiện tại
//        User user = userRepository.getReferenceById(CurrentUserInfoUtil.getCurrentUserId());
        User user = userRepository.findByIdAndDeleteFlagFalse(CurrentUserInfoUtil.getCurrentUserId())
                .orElseThrow(() -> new AppException(UserErrorCode.ACCOUNT_NOT_FOUND));

        if (user.getRole().getName().toString().equals(ERole.INTERN.name())) {
            throw new AppException(AuthErrorCode.UNAUTHORIZED);
        }

        //tao object paper
        Paper paper = Paper.builder()
                .s3Key(request.s3Key())
                .user(user)
                .totalPage(request.totalPage())
                .authorName(request.authorName())
                .title(request.title())
                .publicationYear(request.publicationYear())
                .doi(request.doi())
                .build();
        // lưu object paper vào db (bao gồm cả key)
        paperRepository.save(paper);
        return new AddPaperResponse(paper.getId());
    }

    @Override
    public S3SignedUrlResponse getDownloadUrl(String s3Key) {
        //todo, kiểm tra paper truowcs ddax, ddax bij xoa chua chang hanj, hinh nhuw ham nay ko duoj suw dun
        String url = fileStorageHelper.generateDownloadFileUrl(s3Key);
        return new S3SignedUrlResponse(url);
    }

    @Override
    public PaperInfoResponse getPaperInfo(String id) {
        Paper paper = paperRepository.findById(id)
                .orElseThrow(() -> new AppException(PaperErrorCode.PAPER_NOT_FOUND));

        if (paper.isDeleteFlag()) throw new AppException(PaperErrorCode.PAPER_IS_DELETED);

        return paperMapper.toPaperInfoResponse(paper);
    }

    @Override
    public List<PaperInfoResponse> getMyPapersOfCurrentUser() {
        String currentUserId = CurrentUserInfoUtil.getCurrentUserId();
        if (currentUserId == null) {
            throw new AppException(UserErrorCode.UN_AUTHENTICATED);
        }

        return paperRepository
                .findByUser_IdAndDeleteFlagFalse(currentUserId)
                .stream()
                .map(paper -> PaperInfoResponse.builder()
                        .id(paper.getId())
                        .s3Key(paper.getS3Key())
                        .totalPage(paper.getTotalPage())
                        .currentPage(paper.getCurrentPage())
                        .authorName(paper.getAuthorName())
                        .title(paper.getTitle())
                        .publicationYear(paper.getPublicationYear())
                        .doi(paper.getDoi())
                        .build())
                .toList();
    }
}
